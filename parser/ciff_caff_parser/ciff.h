#pragma once

// header for CIFF structure

#include <stdio.h>
#include <iostream>
#include <stdint.h>
#include <vector>
#include <string>
#include <cctype>
#include <inttypes.h>

using namespace std;

struct RGB {
public:
	unsigned char R;
	unsigned char G;
	unsigned char B;

	RGB();
	~RGB();
};

struct CiffHeader {
private:
	char magic[5];
	uint64_t header_size;
	uint64_t content_size;
	uint64_t width;
	uint64_t height;
	string caption;
	vector<string> tags;

public:
	CiffHeader();
	~CiffHeader();

	void setMagic(char* m);
	char* getMagic();

	void setHeaderSize(uint64_t hs);
	uint64_t getHeaderSize();

	void setContentSize(uint64_t cs);
	uint64_t getContentSize();

	void setWidth(uint64_t w);
	uint64_t getWidth();

	void setHeight(uint64_t h);
	uint64_t getHeight();

	void setCaption(string c);
	string getCaption();

	void setTags(vector<string> t);
	vector<string> getTags();
};

struct CiffContent {
private:
	vector<vector<RGB>> pixels;

public:
	CiffContent();
	~CiffContent();

	void setPixels(vector<vector<RGB>> p);
	vector<vector<RGB>> getPixels();
};

struct Ciff {
private:
	CiffHeader ciff_header;
	CiffContent ciff_content;

	vector<char> slice(vector<char> const& in, uint64_t from, uint64_t to);
	char* vectorToString(vector<char> in);
	uint64_t vectorToInt(vector<char> in);
	uint64_t parseCaption(vector<char> in, uint64_t from);
	void parseTags(vector<char> in, uint64_t from, uint64_t to);
	void parseContent(vector<char> in, uint64_t from, uint64_t to, uint64_t width, uint64_t height, int filenameIndex);
	
	void initBitmap(uint64_t height, uint64_t width, int filenameIndex);
	unsigned char* createBitmapFileHeader(uint64_t height, uint64_t stride);
	unsigned char* createBitmapInfoHeader(uint64_t height, uint64_t width);

public:
	Ciff();
	~Ciff();

	void saveCiffPartsToVariables(vector<char> animation, int filenameIndex);
	void generateBitmapImage(unsigned char* image, uint64_t height, uint64_t width, char* imageFileName);
};