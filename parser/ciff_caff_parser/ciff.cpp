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
}