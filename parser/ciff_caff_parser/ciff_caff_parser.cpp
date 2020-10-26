// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>
#include <inttypes.h>

using namespace std;

vector<char> readFile(string fileName);
uint64_t parseBlock(vector<char> content, uint64_t index);
vector<char> slice(vector<char> const &in, int from, int to);
char* vector_to_string(vector<char> in);
uint64_t vector_to_int(vector<char> in);
void parseHeader(vector<char>, uint64_t block_length);
void parseCredits(vector<char>, uint64_t block_length);
void parseAnimation(vector<char>, uint64_t block_length);

int main(void) {
    const char fileName[] = "caff_files/3.caff";

    vector<char> content;

    try {
        content = readFile(fileName);
    }
    catch (string e) {
        cout << e << "\n";
        return -1;
    }

    uint64_t index = 0;

    while (index < content.size()) {
        index = parseBlock(content, index);
    }
}

vector<char> readFile(string fileName) {
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

uint64_t parseBlock(vector<char> content, uint64_t index) {
    uint64_t block_type = content[index];
    uint64_t block_length = vector_to_int(slice(content, index + 1, index + 8));
    vector<char> block = slice(content, index + 9, index + 9 + block_length - 1);

    if (block_type == 1) {
        printf("HEADER\n");
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
        parseAnimation(block, block_length);
    }

    return index + 9 + block_length;
}

vector<char> slice(vector<char> const &in, int from, int to) {
    auto start = in.begin() + from;
    auto end = in.begin() + to + 1;
    vector<char> vec(start, end);

    return vec;
}

char* vector_to_string(vector<char> in) {
    int size = in.size();
    char* tmp = new char[size + 1];
    
    for (int i = 0; i < size; i++)
    {
        tmp[i] = in[i];
    }

    tmp[size] = '\0';
    return tmp;
}

uint64_t vector_to_int(vector<char> in) {
    return *((uint64_t*) vector_to_string(in));
}

void parseHeader(vector<char> block, uint64_t block_length) {
    char* magic = vector_to_string(slice(block, 0, 3));
    printf("Magic: %s\n", magic);

    uint64_t header_size = vector_to_int(slice(block, 4, 11));
    printf("Header size: %" PRIu64 "\n", header_size);

    uint64_t num_anim = vector_to_int(slice(block, 12, block_length - 1));
    printf("Number of animations: %" PRIu64 "\n", num_anim);
}

void parseCredits(vector<char> block, uint64_t block_length) {
    uint64_t year = vector_to_int(slice(block, 0, 1));
    printf("Year: %" PRIu64 "\n", year);

    uint64_t month = block[2];
    printf("Month: %" PRIu64 "\n", month);

    uint64_t day = block[3];
    printf("Day: %" PRIu64 "\n", day);

    uint64_t hour = block[4];
    printf("Hour: %" PRIu64 "\n", hour);

    uint64_t minute = block[5];
    printf("Minute: %" PRIu64 "\n", minute);

    uint64_t creator_len = vector_to_int(slice(block, 6, 13));
    printf("Creator len: %" PRIu64 "\n", creator_len);

    char* creator = vector_to_string(slice(block, 14, block_length - 1));
    printf("Creator: %s\n", creator);
}

void parseAnimation(vector<char> block, uint64_t block_length) {
    uint64_t duration = vector_to_int(slice(block, 0, 8));
    printf("Duration: %" PRIu64 "\n", duration);

    char* ciff = vector_to_string(slice(block, 9, block_length - 1));
}
