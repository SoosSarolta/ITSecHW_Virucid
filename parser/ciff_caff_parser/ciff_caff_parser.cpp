// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>
#include <inttypes.h>

#include "caff.h"

using namespace std;

int main(int argc, char* argv[]) {
    if (argc < 2) {
        printf("No filename specified, exiting...\n");
        return -1;
    }

    vector<char> content;
    Caff *caff = new Caff();

    try {
        content = caff->readFile(argv[1]);
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

    return 0;
}