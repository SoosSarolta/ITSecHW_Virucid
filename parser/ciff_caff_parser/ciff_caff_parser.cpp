// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>
#include <inttypes.h>

#include "caff.h"

using namespace std;

int main(void) {
    const char fileName[] = "caff_files/3.caff";

    vector<char> content;

    Caff *caff = new Caff();

    try {
        content = caff->readFile(fileName);
    }
    catch (string e) {
        cout << e << "\n";
        return -1;
    }

    uint64_t index = 0;
    int i = 0;

    while (index < content.size()) {
        index = caff->parseBlock(content, index, i);
        i++;
    }
}