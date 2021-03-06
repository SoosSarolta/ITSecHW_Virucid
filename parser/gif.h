#pragma once

#include <stdio.h>   // for FILE*
#include <string.h>  // for memcpy and bzero
#include <stdint.h>  // for integer typedefs

// Define these macros to hook into a custom memory allocator.
// TEMP_MALLOC and TEMP_FREE will only be called in stack fashion - frees in the reverse order of mallocs
// and any temp memory allocated by a function will be freed before it exits.
// MALLOC and FREE are used only by GifBegin and GifEnd respectively (to allocate a buffer the size of the image, which
// is used to find changed pixels for delta-encoding.)

#ifndef GIF_TEMP_MALLOC
#include <stdlib.h>
#define GIF_TEMP_MALLOC malloc
#endif

#ifndef GIF_TEMP_FREE
#include <stdlib.h>
#define GIF_TEMP_FREE free
#endif

#ifndef GIF_MALLOC
#include <stdlib.h>
#define GIF_MALLOC malloc
#endif

#ifndef GIF_FREE
#include <stdlib.h>
#define GIF_FREE free
#endif

const int kGifTransIndex = 0;

struct Palette {
public:
    int bitDepth;

    uint8_t r[256];
    uint8_t g[256];
    uint8_t b[256];

    // k-d tree over RGB space, organized in heap fashion
    // i.e. left child of node i is node i*2, right child is node i*2+1
    // nodes 256-511 are implicitly the leaves, containing a color
    uint8_t treeSplitElt[256];
    uint8_t treeSplit[256];
};

// Simple structure to write out the LZW-compressed portion of the image
// one bit at a time
struct BitStatus {
    uint8_t bitIndex;  // how many bits in the partial byte written so far
    uint8_t byte;      // current partial byte

    uint32_t chunkIndex;
    uint8_t chunk[256];   // bytes are written in here until we have 256 of them, then written to the file
};

// The LZW dictionary is a 256-ary tree constructed as the file is encoded,
// this is one node
struct GifLzwNode {
    uint16_t m_next[256];
};

struct GifWriter {
    FILE* f;
    uint8_t* oldImage;
    bool firstFrame;
};

struct GIF {
private:
    Palette palette;
    GifWriter* writer;
    BitStatus bitStatus;

    uint64_t width;
    uint64_t height;

    int GifIMax(int l, int r);
    int GifIMin(int l, int r);
    int GifIAbs(int i);

    void GifGetClosestPaletteColor(int r, int g, int b, int& bestInd, int& bestDiff, int treeRoot = 1);
    void GifSwapPixels(uint8_t* image, int pixA, int pixB);
    int GifPartition(uint8_t* image, const int left, const int right, const int elt, int pivotIndex);
    void GifPartitionByMedian(uint8_t* image, int left, int right, int com, int neededCenter);
    void GifSplitPalette(uint8_t* image, int numPixels, int firstElt, int lastElt, int splitElt, int splitDist, int treeNode, bool buildForDither);
    int GifPickChangedPixels(const uint8_t* lastFrame, uint8_t* frame, int numPixels);
    void GifMakePalette(const uint8_t* lastFrame, const uint8_t* nextFrame, int bitDepth, bool buildForDither);
    void GifDitherImage(const uint8_t* lastFrame, const uint8_t* nextFrame, uint8_t* outFrame);
    void GifThresholdImage(const uint8_t* lastFrame, const uint8_t* nextFrame, uint8_t* outFrame);
    void GifWriteBit(uint32_t bit);
    void GifWriteChunk(FILE* f);
    void GifWriteCode(FILE* f, uint32_t code, uint32_t length);
    void GifWritePalette(FILE* f);
    void GifWriteLzwImage(FILE* f, uint8_t* image, uint32_t left, uint32_t top, uint32_t delay);

public:
    GIF(uint64_t width, uint64_t height);
    GIF(const GIF& g);
    ~GIF();

    bool GifBegin(const char* filename, uint32_t delay, int32_t bitDepth = 8, bool dither = false);
    bool GifEnd();
    bool GifWriteFrame(const uint8_t* image, uint32_t delay, int bitDepth = 8, bool dither = false);
};