#pragma once

#include <inttypes.h>
#include <iostream>


struct Bitmap {
private:
	unsigned char* createBitmapFileHeader(uint64_t height, uint64_t stride);
	unsigned char* createBitmapInfoHeader(uint64_t height, uint64_t width);
public:
	void generateBitmapImage(unsigned char* image, uint64_t height, uint64_t width, const char* imageFileName);

};
