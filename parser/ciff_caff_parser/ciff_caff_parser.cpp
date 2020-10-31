// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>
#include <inttypes.h>

#include "caff.h"
#include "bitmap.h"

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

    vector<Ciff*> ciffs = caff->getCaffAnimation().getCiffs();
    int numOfCiffs = ciffs.size();

    uint64_t width = ciffs[0]->getCiffHeader().getWidth();
    uint64_t height = ciffs[0]->getCiffHeader().getHeight();
    for (int i = 0; i < numOfCiffs; i++) {
        vector <vector<RGB>> rows = ciffs[i]->getCiffContent().getPixels();
        uint64_t x, y;

        unsigned char* image = new unsigned char[height * width * 3];

        string imageFileNameFirst = "ciffBitmapImage";
        string imageFileNameIndex = to_string(i);
        string imageFileNameBmp = ".bmp";

        string imageFileNameString = imageFileNameFirst + imageFileNameIndex + imageFileNameBmp;

        const char* imageFileName = imageFileNameString.c_str();

        for (x = 0; x < height; x++) {
            for (y = 0; y < width; y++) {
                image[x * 3 * width + y * 3 + 2] = (unsigned char)((rows.at(x)).at(y)).B;
                image[x * 3 * width + y * 3 + 1] = (unsigned char)((rows.at(x)).at(y)).G;
                image[x * 3 * width + y * 3 + 0] = (unsigned char)((rows.at(x)).at(y)).R;

            }
        }
        Bitmap* bitmap = new Bitmap();
        bitmap->generateBitmapImage((unsigned char*)image, height, width, (const char*)imageFileName);
        printf("\nBitmap image generated!\n");
    }
    return 0;
}