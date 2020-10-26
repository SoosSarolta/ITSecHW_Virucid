// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>

using namespace std;

vector<char> slice(vector<char> in, int from, int to);
char* vector_to_string(vector<char> in);
uint64_t vector_to_int(vector<char> in);

int main(void)
{
    const char filename[] = "caff_files\\1.caff";

    ifstream in;

    in.open(filename, ios::in | ios::binary);

    string s;

    if (in.is_open())
    {
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
        uint64_t id = vector_to_int(slice(contents, 0, 1));
        printf("ID: %d\n", id);

        uint64_t length = vector_to_int(slice(contents, 1, 9));
        printf("Length: %d\n", length);

        vector<char> header = slice(contents, 9, 9 + length);

        char* magic = vector_to_string(slice(header, 0, 8));
        printf("Magic: %s\n", magic);

        uint64_t header_size = vector_to_int(slice(header, 4, 12));
        printf("Header size: %d\n", header_size);

        uint64_t num_anim = vector_to_int(slice(contents, 12, 20));
        printf("Number of animations: %d\n", num_anim);
    }
}

vector<char> slice(vector<char> in, int from, int to)
{
    return vector<char>(in.begin() + from, in.begin() + to);
}

char* vector_to_string(vector<char> in)
{
    int size = in.size();
    char* tmp = new char[size];
    
    for (int i = 0; i < size; i++)
    {
        tmp[i] = in[i];
    }

    return tmp;
}

uint64_t vector_to_int(vector<char> in)
{
    int size = in.size();
    char* tmp = new char[size];

    for (int i = 0; i < size; i++)
    {
        tmp[i] = in[i];
    }

    return *((uint64_t*) tmp);
}