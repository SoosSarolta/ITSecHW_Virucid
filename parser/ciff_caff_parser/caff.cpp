// class for caff structure

#include "caff.h"

using namespace std;

CaffHeader::CaffHeader() {
	magic[0] = magic[1] = magic[2] = magic[3] = 0;
	header_size = 0;
	num_anim = 0;
}

CaffHeader::~CaffHeader() {}

void CaffHeader::setMagic(char* m) {
	strncpy_s(magic, 4, m, 4);
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
	ciff = nullptr;
}

CaffAnimation::~CaffAnimation() {}

void CaffAnimation::setDuration(uint64_t d) {
	duration = d;
}

uint64_t CaffAnimation::getDuration() {
	return duration;
}

void CaffAnimation::setCiff(unique_ptr<Ciff> c) {
	make_unique<Ciff>(c);
}

unique_ptr<Ciff> CaffAnimation::getCiff() {
	return make_unique<Ciff>(ciff);
}


Caff::Caff() {
	caff_header = CaffHeader::CaffHeader();
	caff_credits = CaffCredits::CaffCredits();
	caff_animations = vector<CaffAnimation>();
}

Caff::~Caff() {}

void Caff::saveCaffPartsToVariables() {
	// TODO parse CAFF input and save the parts into the suitable variables
}

void Caff::createPreview() {
	// TODO parse CAFF and create preview image for webshop use
}