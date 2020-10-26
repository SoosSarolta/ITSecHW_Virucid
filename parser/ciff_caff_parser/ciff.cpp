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
	magic[0] = magic[1] = magic[2] = magic[3] = 0;
	header_size = 0;
	content_size = 0;
	width = 0;
	height = 0;
	caption = string("");
	tags = vector<string>(0);
}

CiffHeader::~CiffHeader() {}

void CiffHeader::setMagic(char* m) {
	strncpy_s(magic, 4, m, 4);
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

void Ciff::saveCiffPartsToVariables() {
	// TODO parse CIFF input and save the parts into the suitable variables
}