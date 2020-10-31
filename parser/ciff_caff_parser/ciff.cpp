// class for ciff structure

#include "ciff.h"

using namespace std;

RGB::RGB() {
	R = 0;
	G = 0;
	B = 0;
}

RGB::~RGB() {}


CiffHeader::CiffHeader() {
	magic[0] = magic[1] = magic[2] = magic[3] = magic[4] = 0;
	header_size = 0;
	content_size = 0;
	width = 0;
	height = 0;
	caption = string("");
	tags = vector<string>(0);
}

CiffHeader::~CiffHeader() {}

void CiffHeader::setMagic(char* m) {
	strncpy_s(magic, 5, m, 5);
}

char* CiffHeader::getMagic() {
	return magic;
}

void CiffHeader::setHeaderSize(uint64_t hs) {
	header_size = hs;
}

uint64_t CiffHeader::getHeaderSize() {
	return header_size;
}

void CiffHeader::setContentSize(uint64_t cs) {
	content_size = cs;
}

uint64_t CiffHeader::getContentSize() {
	return content_size;
}

void CiffHeader::setWidth(uint64_t w) {
	width = w;
}

uint64_t CiffHeader::getWidth() {
	return width;
}

void CiffHeader::setHeight(uint64_t h) {
	height = h;
}

uint64_t CiffHeader::getHeight() {
	return height;
}

void CiffHeader::setCaption(string c) {
	caption = c;
}

string CiffHeader::getCaption() {
	return caption;
}

void CiffHeader::setTags(vector<string> t) {
	tags = t;
}

vector<string> CiffHeader::getTags() {
	return tags;
}


CiffContent::CiffContent() {
	pixels = vector<vector<RGB>>(0);
}

CiffContent::~CiffContent() {}

void CiffContent::setPixels(vector<vector<RGB>> p) {
	pixels = p;
}

vector<vector<RGB>> CiffContent::getPixels() {
	return pixels;
}


Ciff::Ciff() {
	ciff_header = CiffHeader::CiffHeader();
	ciff_content = CiffContent::CiffContent();
}

Ciff::~Ciff() {}

CiffHeader Ciff::getCiffHeader() {
	return ciff_header;
}

CiffContent Ciff::getCiffContent() {
	return ciff_content;
}

void Ciff::saveCiffPartsToVariables(vector<char> animation, int filenameIndex) {
	printf("*******************\n");
	printf("CIFF PARSING\n");

	char* magic = vectorToString(slice(animation, 0, 3));
	ciff_header.setMagic(magic);
	printf("Magic: %s\n", magic);

	uint64_t header_size = vectorToInt(slice(animation, 4, 11));
	ciff_header.setHeaderSize(header_size);
	printf("Header size: %" PRIu64 "\n", header_size);

	uint64_t content_size = vectorToInt(slice(animation, 12, 19));
	ciff_header.setContentSize(content_size);
	printf("Content size: %" PRIu64 "\n", content_size);

	uint64_t width = vectorToInt(slice(animation, 20, 27));
	ciff_header.setWidth(width);
	printf("Width: %" PRIu64 "\n", width);

	uint64_t height = vectorToInt(slice(animation, 28, 35));
	ciff_header.setHeight(height);
	printf("Height: %" PRIu64 "\n", height);

	uint64_t index = parseCaption(animation, 36);

	parseTags(animation, index + 1, header_size);

	parseContent(animation, header_size, animation.size(), width, height, filenameIndex);

	printf("*******************\n");
}

vector<char> Ciff::slice(vector<char> const& in, uint64_t from, uint64_t to) {
	auto start = in.begin() + from;
	auto end = in.begin() + to + 1;
	vector<char> vec(start, end);

	return vec;
}

char* Ciff::vectorToString(vector<char> in) {
	uint64_t size = in.size();
	char* tmp = new char[size + 1];

	for (int i = 0; i < size; i++) {
		tmp[i] = in[i];
	}

	tmp[size] = '\0';
	return tmp;
}

uint64_t Ciff::vectorToInt(vector<char> in) {
	return *((uint64_t*)vectorToString(in));
}

uint64_t Ciff::parseCaption(vector<char> in, uint64_t from) {
	int size = in.size();
	uint64_t i = from;
	char* tmp = new char[size];

	for (i; i < size; i++) {
		tmp[i - from] = in[i];
		if (in[i] == '\n') {
			break;
		}
	}

	tmp[i - from + 1] = '\0';
	ciff_header.setCaption(tmp);
	printf("Caption: %s", tmp);

	return i;
}

void Ciff::parseTags(vector<char> in, uint64_t from, uint64_t to) {
	vector<string> tags;
	char* tmp = new char[to - from];
	uint64_t index = 0;

	for (uint64_t i = from; i < to; i++, index++) {
		tmp[index] = in[i];
		if (in[i] == '\0') {
			tags.push_back(tmp);
			index = -1;
		}
	}

	ciff_header.setTags(tags);

	cout << "Tags: ";
	for (string tag : tags) {
		cout << tag << "; ";
	}
	cout << "\n";
}

void Ciff::parseContent(vector<char> in, uint64_t from, uint64_t to, uint64_t width, uint64_t height, int filenameIndex) {
	uint64_t row = 0;
	uint64_t col = 0;

	vector<RGB> pixel_row;
	vector <vector<RGB>> rows;

	for (uint64_t i = from; i < to; i += 3, col++) {
		if (width <= col) {
			row++;
			col = 0;
			rows.push_back(pixel_row);
			pixel_row.clear();
		}

		RGB rgb;
		rgb.R = in[i];
		rgb.G = in[i + 1];
		rgb.B = in[i + 2];

		pixel_row.push_back(rgb);
	}

	rows.push_back(pixel_row);
	ciff_content.setPixels(rows);

	initBitmap(height, width, filenameIndex);
}

void Ciff::initBitmap(uint64_t height, uint64_t width, int filenameIndex) {
	unsigned char* image = new unsigned char[height * width * 3];

	string imageFileNameFirst = "ciffBitmapImage";
	string imageFileNameIndex = to_string(filenameIndex);
	string imageFileNameBmp = ".bmp";

	string imageFileNameString = imageFileNameFirst + imageFileNameIndex + imageFileNameBmp;

	const char* imageFileName = imageFileNameString.c_str();

	vector <vector<RGB>> rows = ciff_content.getPixels();

	uint64_t x, y;
	for (x = 0; x < height; x++) {
		for (y = 0; y < width; y++) {
			image[x * 3 * width + y * 3 + 2] = (unsigned char)((rows.at(x)).at(y)).B;
			image[x * 3 * width + y * 3 + 1] = (unsigned char)((rows.at(x)).at(y)).G;
			image[x * 3 * width + y * 3 + 0] = (unsigned char)((rows.at(x)).at(y)).R;
		}
	}

	generateBitmapImage((unsigned char*)image, height, width, (const char*)imageFileName);
	printf("\nBitmap image generated!\n");
}

void Ciff::generateBitmapImage(unsigned char* image, uint64_t height, uint64_t width, const char* imageFileName) {
	int widthInBytes = width * 3;

	unsigned char padding[3] = { 0, 0, 0 };
	int paddingSize = (4 - (widthInBytes) % 4) % 4;

	int stride = (widthInBytes) + paddingSize;

	FILE* imageFile;
	errno_t err = fopen_s(&imageFile, imageFileName, "wb");
	if (err != 0) {
		printf("File could not be opened!");
		return;
	}

	if (imageFile != nullptr) {
		unsigned char* fileHeader = createBitmapFileHeader(height, stride);
		fwrite(fileHeader, 1, 14, imageFile);

		unsigned char* infoHeader = createBitmapInfoHeader(height, width);
		fwrite(infoHeader, 1, 40, imageFile);

		uint64_t i;
		for (i = 0; i < height; i++) {
			fwrite(image + (i * widthInBytes), 3, width, imageFile);
			fwrite(padding, 1, paddingSize, imageFile);
		}

		fclose(imageFile);
	}
}

unsigned char* Ciff::createBitmapFileHeader(uint64_t height, uint64_t stride) {
	int fileSize = 14 + 40 + (stride * height);

	static unsigned char fileHeader[] = {
		0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
	};

	fileHeader[0] = (unsigned char)('B');
	fileHeader[1] = (unsigned char)('M');
	fileHeader[2] = (unsigned char)(fileSize);
	fileHeader[3] = (unsigned char)(fileSize >> 8);
	fileHeader[4] = (unsigned char)(fileSize >> 16);
	fileHeader[5] = (unsigned char)(fileSize >> 24);
	fileHeader[10] = (unsigned char)(14 + 40);

	return fileHeader;
}

unsigned char* Ciff::createBitmapInfoHeader(uint64_t height, uint64_t width) {
	static unsigned char infoHeader[] = {
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0,
		0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
		0, 0, 0, 0,
	};

	// bmp is originally stored upside-down => turn it around
	int h = height * (-1);

	infoHeader[0] = (unsigned char)(40);
	infoHeader[4] = (unsigned char)(width);
	infoHeader[5] = (unsigned char)(width >> 8);
	infoHeader[6] = (unsigned char)(width >> 16);
	infoHeader[7] = (unsigned char)(width >> 24);
	infoHeader[8] = (unsigned char)(h);
	infoHeader[9] = (unsigned char)(h >> 8);
	infoHeader[10] = (unsigned char)(h >> 16);
	infoHeader[11] = (unsigned char)(h >> 24);
	infoHeader[12] = (unsigned char)(1);
	infoHeader[14] = (unsigned char)(3 * 8);

	return infoHeader;
}