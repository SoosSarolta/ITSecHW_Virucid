#pragma once

// header for CIFF structure

#include <stdio.h>
#include <iostream>
#include <stdint.h>
#include <vector>
#include <string>
#include <cctype>

using namespace std;

struct RGB {
private:
	unsigned char R;
	unsigned char G;
	unsigned char B;

public:
	RGB();
	~RGB();
};

struct CiffHeader {
private:
	char magic[4];
	uint64_t header_size;
	uint64_t content_size;
	uint64_t width;
	uint64_t height;
	char* caption;
	vector<string> tags;

public:
	CiffHeader();
	~CiffHeader();

	void setMagic(char* magic);
	char* getMagic();

	void setHeaderSize(uint64_t header_size);
	uint64_t getHeaderSize();

	void setContentSize(uint64_t content_size);
	uint64_t getContentSize();

	void setWidth(uint64_t width);
	uint64_t getWidth();

	void setHeight(uint64_t height);
	uint64_t getHeight();

	void setCaption(char* caption);
	char* getCaption();

	void setTags(vector<string> tags);
	vector<string> getTags();
};

struct CiffContent {
private:
	vector<vector<RGB>> pixels;

public:
	CiffContent();
	~CiffContent();

	void setPixels(vector<vector<RGB>> pixels);
	vector<vector<RGB>> getPixels();
};

struct Ciff {
private:
	CiffHeader ciff_header;
	CiffContent ciff_content;

public:
	Ciff();
	~Ciff();
};