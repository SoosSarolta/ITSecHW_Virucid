// ciff_caff_parser.cpp : This file contains the 'main' function. Program execution begins and ends there.

#include <iostream>
#include <fstream>
#include <vector>
#include <iterator>
#include <inttypes.h>

#include "caff.h"
#include "bitmap.h"
#include "gif.h"

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
    catch (string& e) {
        cout << e << "\n";
        return -1;
    }

    uint64_t index = 0;
    int i = 0;

    while (index < content.size()) {
        index = caff->parseBlock(content, index, i);
        i++;
    }

    printf("All blocks are parsed, generating images...\n");

    vector<Ciff*> ciffs = caff->getCaffAnimation().getCiffs();
    int numOfCiffs = ciffs.size();

    uint64_t width = ciffs[0]->getCiffHeader().getWidth();
    uint64_t height = ciffs[0]->getCiffHeader().getHeight();

    auto fileName = "caff.gif";
    vector<uint8_t> gifImage;
    int delay = 10;

    GIF* g = new GIF(width, height);

    g->GifBegin(fileName, delay);

    for (int j = 0; j < numOfCiffs; j++) {
        printf("\nGenerating the %d. bitmap image...\n", j + 1);
        vector <vector<RGB>> rows = ciffs[j]->getCiffContent().getPixels();
        uint64_t x, y;

        Bitmap* bitmap = new Bitmap(new unsigned char[height * width * 3], "ciffBitmapImage", "0", ".bmp");
        bitmap->setFileNameIndex(j);

        for (x = 0; x < height; x++) {
            for (y = 0; y < width; y++) {
                bitmap->getImage()[x * 3 * width + y * 3 + 2] = (unsigned char)((rows.at(x)).at(y)).B;
                bitmap->getImage()[x * 3 * width + y * 3 + 1] = (unsigned char)((rows.at(x)).at(y)).G;
                bitmap->getImage()[x * 3 * width + y * 3 + 0] = (unsigned char)((rows.at(x)).at(y)).R;

                gifImage.push_back((uint8_t)((rows.at(x)).at(y)).B);
                gifImage.push_back((uint8_t)((rows.at(x)).at(y)).G);
                gifImage.push_back((uint8_t)((rows.at(x)).at(y)).R);
                gifImage.push_back((uint8_t)0);
            }
        }
        bitmap->generateBitmapImage((unsigned char*)bitmap->getImage(), height, width, bitmap->getFileName().c_str());
        printf("\nBitmap image generated!\n");

        g->GifWriteFrame(gifImage.data(), delay);
        delete bitmap;
    }

    g->GifEnd();
    printf("\nGif animation generated!\n");

    delete caff;
    delete g;

    return 0;
}