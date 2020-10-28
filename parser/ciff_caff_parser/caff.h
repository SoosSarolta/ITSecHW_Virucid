#pragma once

// header for CAFF structure

#include <stdio.h>
#include <iostream>
#include <stdint.h>
#include <vector>
#include <string>
#include <cctype>
#include <fstream>
#include <iterator>
#include <inttypes.h>

#include "ciff.h"

using namespace std;

struct CaffHeader {
private:
	char magic[5];
	uint64_t header_size;
	uint64_t num_anim;

public:
	CaffHeader();
	~CaffHeader();

	void setMagic(char* m);
	char* getMagic();

	void setHeaderSize(uint64_t hs);
	uint64_t getHeaderSize();

	void setNumAnim(uint64_t na);
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
	string creator;

public:
	CaffCredits();
	~CaffCredits();

	void setCreationYear(uint16_t cy);
	uint16_t getCreationYear();

	void setCreationMonth(uint8_t cm);
	uint8_t getCreationMonth();

	void setCreationDay(uint8_t cd);
	uint8_t getCreationDay();

	void setCreationHour(uint8_t ch);
	uint8_t getCreationHour();

	void setCreationMinute(uint8_t cm);
	uint8_t getCreationMinute();

	void setCreatorLen(uint64_t cl);
	uint64_t getCreatorLen();

	void setCreator(string c);
	string getCreator();
};

struct CaffAnimation {
private:
	uint64_t duration;
	vector<Ciff*> ciffs;	// ??

public:
	CaffAnimation();
	~CaffAnimation();

	void setDuration(uint64_t d);
	uint64_t getDuration();

	void setCiffs(vector<Ciff*> cs);
	const vector<Ciff*> getCiffs();

	void addCiff(Ciff* c);
	const Ciff* getCiff(uint64_t index);
};

struct Caff {
private:
	CaffHeader caff_header;
	CaffCredits caff_credits;
	CaffAnimation caff_animation;

	vector<char> slice(vector<char> const& in, uint64_t from, uint64_t to);
	char* vectorToString(vector<char> in);
	uint64_t vectorToInt(vector<char> in);

	void parseHeader(vector<char> block, uint64_t block_length);
	void parseCredits(vector<char> block, uint64_t block_length);
	void parseAnimation(vector<char> block, uint64_t block_length);
public:
	Caff();
	~Caff();

	vector<char> readFile(string fileName);
	uint64_t parseBlock(vector<char> content, uint64_t index);

	void createPreview();
};