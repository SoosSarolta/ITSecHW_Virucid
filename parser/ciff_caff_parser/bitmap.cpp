#include "bitmap.h"

Bitmap::Bitmap(unsigned char* image) {
	this->image = image;

	namePrefix = "ciffBitmapImage";
	nameIndex = "0";
	namePostfix = ".bmp";
}

Bitmap::~Bitmap() {
	delete image;
}

unsigned char* Bitmap::getImage() {
	return image;
}

void Bitmap::setFileNameIndex(int index) {
	this->nameIndex = to_string(index);
}

string Bitmap::getFileName() {
	string imageFileNameString = namePrefix + nameIndex + namePostfix;
	return imageFileNameString;
}

void Bitmap::generateBitmapImage(unsigned char* image, uint64_t height, uint64_t width, const char* imageFileName) {
	int widthInBytes = width * 3;

	unsigned char padding[3] = { 0, 0, 0 };
	int paddingSize = (4 - (widthInBytes) % 4) % 4;

	int stride = (widthInBytes)+paddingSize;

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

unsigned char* Bitmap::createBitmapFileHeader(uint64_t height, uint64_t stride) {
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

unsigned char* Bitmap::createBitmapInfoHeader(uint64_t height, uint64_t width) {
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