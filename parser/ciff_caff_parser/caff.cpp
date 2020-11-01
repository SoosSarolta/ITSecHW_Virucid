// class for caff structure

#include "caff.h"

using namespace std;

CaffHeader::CaffHeader() {
	magic[0] = magic[1] = magic[2] = magic[3] = magic[4] = 0;
	header_size = 0;
	num_anim = 0;
}

CaffHeader::~CaffHeader() {}

void CaffHeader::setMagic(char* m) {
	strncpy(magic, m, 5);
}

char* CaffHeader::getMagic() {
	return magic;
}

void CaffHeader::setHeaderSize(uint64_t hs) {
	header_size = hs;
}

uint64_t CaffHeader::getHeaderSize() {
	return header_size;
}

void CaffHeader::setNumAnim(uint64_t na) {
	num_anim = na;
}

uint64_t CaffHeader::getNumAnim() {
	return num_anim;
}


CaffCredits::CaffCredits() {
	creation_year = 0;
	creation_month = 0;
	creation_day = 0;
	creation_hour = 0;
	creation_minute = 0;
	creator_len = 0;
	creator = string("");
}

CaffCredits::~CaffCredits() {}

void CaffCredits::setCreationYear(uint16_t cy) {
	creation_year = cy;
}

uint16_t CaffCredits::getCreationYear() {
	return creation_year;
}

void CaffCredits::setCreationMonth(uint8_t cm) {
	creation_month = cm;
}

uint8_t CaffCredits::getCreationMonth() {
	return creation_month;
}

void CaffCredits::setCreationDay(uint8_t cd) {
	creation_day = cd;
}

uint8_t CaffCredits::getCreationDay() {
	return creation_day;
}

void CaffCredits::setCreationHour(uint8_t ch) {
	creation_hour = ch;
}

uint8_t CaffCredits::getCreationHour() {
	return creation_hour;
}

void CaffCredits::setCreationMinute(uint8_t cm) {
	creation_minute = cm;
}

uint8_t CaffCredits::getCreationMinute() {
	return creation_minute;
}

void CaffCredits::setCreatorLen(uint64_t cl) {
	creator_len = cl;
}

uint64_t CaffCredits::getCreatorLen() {
	return creator_len;
}

void CaffCredits::setCreator(string c) {
	creator = c;
}

string CaffCredits::getCreator() {
	return creator;
}


CaffAnimation::CaffAnimation() {
	duration = 0;
	ciffs = vector<Ciff*>();
}

CaffAnimation::~CaffAnimation() {}

void CaffAnimation::setDuration(uint64_t d) {
	duration = d;
}

uint64_t CaffAnimation::getDuration() {
	return duration;
}

void CaffAnimation::setCiffs(vector<Ciff*> cs) {
	ciffs = cs;
}

const vector<Ciff*> CaffAnimation::getCiffs() {
	return ciffs;
}

void CaffAnimation::addCiff(Ciff* c) {
	ciffs.push_back(c);
}

const Ciff* CaffAnimation::getCiff(uint64_t index) {
	return ciffs[index];
}

Caff::Caff() {
	caff_header = CaffHeader();
	caff_credits = CaffCredits();
	caff_animation = CaffAnimation();
}

Caff::~Caff() {
	for (int i = 0; i < caff_animation.getCiffs().size(); i++) {
		delete caff_animation.getCiffs().at(i);
	}
}

CaffAnimation Caff::getCaffAnimation() {
	return caff_animation;
}

vector<char> Caff::readFile(string fileName) {
	ifstream in;

	in.open(fileName, ios::in | ios::binary);

	if (!in.is_open()) {
		throw "Cannot open file"s;
	}

	streampos start = in.tellg();

	in.seekg(0, std::ios::end);
	streampos end = in.tellg();

	in.seekg(0, std::ios::beg);

	vector<char> contents;
	contents.resize(static_cast<size_t>(end - start));
	in.read(&contents[0], contents.size());

	return contents;
}

uint64_t Caff::parseBlock(vector<char> content, uint64_t index, int filenameIndex) {
	uint64_t block_type = content[index];
	uint64_t block_length = vectorToInt(slice(content, index + 1, index + 8));
	vector<char> block = slice(content, index + 9, index + 9 + block_length - 1);

	if (block_type == 1) {
		printf("\nHEADER\n");
		printf("Block length: %" PRIu64 "\n", block_length);
		parseHeader(block, block_length);
	}
	else if (block_type == 2) {
		printf("\nCREDITS\n");
		printf("Block length: %" PRIu64 "\n", block_length);
		parseCredits(block, block_length);
	}
	else if (block_type == 3) {
		printf("\nANIMATION\n");
		printf("Block length: %" PRIu64 "\n", block_length);
		parseAnimation(block, block_length, filenameIndex);
	}

	return index + 9 + block_length;
}

vector<char> Caff::slice(vector<char> const& in, uint64_t from, uint64_t to) {
	auto start = in.begin() + from;
	auto end = in.begin() + to + 1;
	vector<char> vec(start, end);

	return vec;
}

char* Caff::vectorToString(vector<char> in) {
	uint64_t size = in.size();
	char* tmp = new char[size + 1];

	for (int i = 0; i < size; i++)
	{
		tmp[i] = in[i];
	}

	tmp[size] = '\0';
	return tmp;
}

uint64_t Caff::vectorToInt(vector<char> in) {
	char* tmp = vectorToString(in);
	uint64_t ret = *((uint64_t*)tmp);
	delete[] tmp;
	return ret;
}

void Caff::parseHeader(vector<char> block, uint64_t block_length) {
	char* magic = vectorToString(slice(block, 0, 3));
	caff_header.setMagic(magic);
	printf("Magic: %s\n", magic);
	delete[] magic;

	uint64_t header_size = vectorToInt(slice(block, 4, 11));
	caff_header.setHeaderSize(header_size);
	printf("Header size: %" PRIu64 "\n", header_size);

	uint64_t num_anim = vectorToInt(slice(block, 12, block_length - 1));
	caff_header.setNumAnim(num_anim);
	printf("Number of animations: %" PRIu64 "\n", num_anim);
}

void Caff::parseCredits(vector<char> block, uint64_t block_length) {
	uint16_t year = vectorToInt(slice(block, 0, 1));
	caff_credits.setCreationYear(year);
	printf("Year: %" PRIu16 "\n", year);

	uint8_t month = block[2];
	caff_credits.setCreationMonth(month);
	printf("Month: %" PRIu8 "\n", month);

	uint8_t day = block[3];
	caff_credits.setCreationDay(day);
	printf("Day: %" PRIu8 "\n", day);

	uint8_t hour = block[4];
	caff_credits.setCreationHour(hour);
	printf("Hour: %" PRIu8 "\n", hour);

	uint8_t minute = block[5];
	caff_credits.setCreationMinute(minute);
	printf("Minute: %" PRIu8 "\n", minute);

	uint64_t creator_len = vectorToInt(slice(block, 6, 13));
	caff_credits.setCreatorLen(creator_len);
	printf("Creator len: %" PRIu64 "\n", creator_len);

	char* creator = vectorToString(slice(block, 14, block_length - 1));
	if (creator_len != 0) {
		caff_credits.setCreator(string(creator));
	}
	else {
		caff_credits.setCreator(string(""));
	}
	printf("Creator: %s\n", creator);
	delete[] creator;
}

void Caff::parseAnimation(vector<char> block, uint64_t block_length, int filenameIndex) {
	uint64_t duration = vectorToInt(slice(block, 0, 7));
	caff_animation.setDuration(duration);
	printf("Duration: %" PRIu64 "\n", duration);

	vector<char> animation = slice(block, 8, block_length - 1);

	Ciff* ciff = new Ciff();
	ciff->saveCiffPartsToVariables(animation, filenameIndex);
	caff_animation.addCiff(ciff);
}