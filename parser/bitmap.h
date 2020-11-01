#pragma once

#include <inttypes.h>
#include <iostream>
#include <string>

using namespace std;

struct Bitmap {
private:
	unsigned char* image;

	string namePrefix;
	string nameIndex;
	string namePostfix;

	unsigned char* createBitmapFileHeader(uint64_t height, uint64_t stride);
	unsigned char* createBitmapInfoHeader(uint64_t height, uint64_t width);

public:
	Bitmap(unsigned char* image, const string& namePrefix, const string& nameIndex, const string& namePostfix);
	~Bitmap();

	unsigned char* getImage();
	void setFileNameIndex(int index);
	string getFileName();

	void generateBitmapImage(unsigned char* image, uint64_t height, uint64_t width, const char* imageFileName);
};