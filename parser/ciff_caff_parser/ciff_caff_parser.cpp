// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>

using namespace std;

vector<char> slice(vector<char> const &in, int from, int to);
char* vector_to_string(vector<char> in);
uint64_t vector_to_int(vector<char> in);

int main(void)
{
    const char filename[] = "caff_files\\1.caff";

    ifstream in;

    in.open(filename, ios::in | ios::binary);

    string s;

    if (!in.is_open())
    {
        return -1;
    }

    // get the starting position
    streampos start = in.tellg();

    // go to the end
    in.seekg(0, std::ios::end);

    // get the ending position
    streampos end = in.tellg();

    // go back to the start
    in.seekg(0, std::ios::beg);

    // create a vector to hold the data that
    // is resized to the total size of the file    
    vector<char> contents;
    contents.resize(static_cast<size_t>(end - start));

    // read it in
    in.read(&contents[0], contents.size());

    // print it out (for clarity)
    uint64_t block_type = contents[0];
    printf("ID: %d\n", block_type);

    uint64_t length = vector_to_int(slice(contents, 1, 8));
    printf("Length: %d\n", length);

    vector<char> header = slice(contents, 9, 9 + length - 1);

    char* magic = vector_to_string(slice(header, 0, 3));
    printf("Magic: %s\n", magic);

    uint64_t header_size = vector_to_int(slice(header, 4, 11));
    printf("Header size: %d\n", header_size);

    uint64_t num_anim = vector_to_int(slice(header, 12, 19));
    printf("Number of animations: %d\n\n", num_anim);

    int new_block = 9 + length;

    block_type = contents[new_block];
    printf("ID: %d\n", block_type);

    length = vector_to_int(slice(contents, new_block + 1, new_block + 8));
    printf("Length: %d\n", length);

    vector<char> credits = slice(contents, new_block + 9, new_block + 8 + length);
    uint64_t year = vector_to_int(slice(credits, 0, 1));
    printf("Year: %d\n", year);

    uint64_t month = credits[2];
    printf("Month: %d\n", month);

    uint64_t day = credits[3];
    printf("Day: %d\n", day);

    uint64_t hour = credits[4];
    printf("Hour: %d\n", hour);

    uint64_t minute = credits[5];
    printf("Minute: %d\n", minute);

    uint64_t creator_len = vector_to_int(slice(credits, 6, 13));
    printf("Creator len: %d\n", creator_len);

    char* creator = vector_to_string(slice(credits, 14, 14 + creator_len - 1));
    printf("Creator: %s\n", creator);
}

vector<char> slice(vector<char> const &in, int from, int to)
{
    auto start = in.begin() + from;
    auto end = in.begin() + to + 1;
    vector<char> vec(start, end);

    return vec;
}

char* vector_to_string(vector<char> in)
{
    int size = in.size();
    char* tmp = new char[size + 1];
    
    for (int i = 0; i < size; i++)
    {
        tmp[i] = in[i];
    }

    //printf("%d\n", strtol("abcd", NULL, 16));
    tmp[size] = '\0';
    return tmp;
}

uint64_t vector_to_int(vector<char> in)
{
    return *((uint64_t*) vector_to_string(in));
}