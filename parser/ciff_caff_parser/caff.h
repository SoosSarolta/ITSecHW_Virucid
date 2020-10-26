#pragma once

// header for CAFF structure

#include <stdio.h>
#include <iostream>
#include <stdint.h>
#include <vector>
#include <string>
#include <cctype>

#include "ciff.h"

using namespace std;

struct CaffHeader {
private:
	char magic[4];
	uint64_t header_size;
	uint64_t num_anim;

public:
	CaffHeader();
	~CaffHeader();

	void setMagic(char* magic);
	char* getMagic();

	void setHeaderSize(uint64_t header_size);
	uint64_t getHeaderSize();

	void setNumAnim(uint64_t num_anim);
	uint64_t getNumAnim();
};

struct CaffCredits {
private:
	uint16_t creation_year;
	uint8_t creation_month;
	uint8_t creation_day;
	uint8_t creation_hour;
	uint8_t creation_minute;
	uint64_t creator_len;
	char* creator;

public:
	CaffCredits();
	~CaffCredits();

	void setCreationYear(uint16_t creation_year);
	uint16_t getCreationYear();

	void setCreationMonth(uint8_t creation_month);
	uint8_t getCreationMonth();

	void setCreationDay(uint8_t creation_day);
	uint8_t getCreationDay();

	void setCreationHour(uint8_t creation_hour);
	uint8_t getCreationHour();

	void setCreationMinute(uint8_t creation_minute);
	uint8_t getCreationMinute();

	void setCreatorLen(uint64_t creator_len);
	uint64_t getCreatorLen();

	void setCreator(char* creator);
	char* getCreator();
};

struct CaffAnimation {
private:
	uint64_t duration;
	unique_ptr<Ciff> ciff;

public:
	CaffAnimation();
	~CaffAnimation();

	void setDuration(uint64_t duration);
	uint64_t getDuration();

	void setCiff(unique_ptr<Ciff> ciff);
	unique_ptr<Ciff> getCiff();
};

struct Caff {
private:
	CaffHeader caff_header;
	CaffCredits caff_credits;
	vector<CaffAnimation> caff_animations;

public:
	Caff();
	~Caff();
};