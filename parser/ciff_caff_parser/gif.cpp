#include "gif.h"

GIF::GIF(uint32_t width, uint32_t height) {
    this->width = width;
    this->height = height;

	palette = Palette();
    writer = new GifWriter();
    bitStatus = BitStatus();
}

GIF::~GIF() {}

int GIF::GifIMax(int l, int r) { return l > r ? l : r; }
int GIF::GifIMin(int l, int r) { return l < r ? l : r; }
int GIF::GifIAbs(int i) { return i < 0 ? -i : i; }

// walks the k-d tree to pick the palette entry for a desired color.
// Takes as in/out parameters the current best color and its error -
// only changes them if it finds a better color in its subtree.
// this is the major hotspot in the code at the moment.
void GIF::GifGetClosestPaletteColor(int r, int g, int b, int& bestInd, int& bestDiff, int treeRoot) {
    // base case, reached the bottom of the tree
    if (treeRoot > (1 << palette.bitDepth) - 1) {
        int ind = treeRoot - (1 << palette.bitDepth);
        if (ind == kGifTransIndex) return;

        // check whether this color is better than the current winner
        int r_err = r - ((int32_t)palette.r[ind]);
        int g_err = g - ((int32_t)palette.g[ind]);
        int b_err = b - ((int32_t)palette.b[ind]);
        int diff = GifIAbs(r_err) + GifIAbs(g_err) + GifIAbs(b_err);

        if (diff < bestDiff) {
            bestInd = ind;
            bestDiff = diff;
        }

        return;
    }

    // take the appropriate color (r, g, or b) for this node of the k-d tree
    int comps[3]; comps[0] = r; comps[1] = g; comps[2] = b;
    int splitComp = comps[palette.treeSplitElt[treeRoot]];

    int splitPos = palette.treeSplit[treeRoot];
    if (splitPos > splitComp) {
        // check the left subtree
        GifGetClosestPaletteColor(r, g, b, bestInd, bestDiff, treeRoot * 2);
        if (bestDiff > splitPos - splitComp) {
            // cannot prove there's not a better value in the right subtree, check that too
            GifGetClosestPaletteColor(r, g, b, bestInd, bestDiff, treeRoot * 2 + 1);
        }
    }
    else {
        GifGetClosestPaletteColor(r, g, b, bestInd, bestDiff, treeRoot * 2 + 1);
        if (bestDiff > splitComp - splitPos) {
            GifGetClosestPaletteColor(r, g, b, bestInd, bestDiff, treeRoot * 2);
        }
    }
}

void GIF::GifSwapPixels(uint8_t* image, int pixA, int pixB) {
    uint8_t rA = image[pixA * 4];
    uint8_t gA = image[pixA * 4 + 1];
    uint8_t bA = image[pixA * 4 + 2];
    uint8_t aA = image[pixA * 4 + 3];

    uint8_t rB = image[pixB * 4];
    uint8_t gB = image[pixB * 4 + 1];
    uint8_t bB = image[pixB * 4 + 2];
    uint8_t aB = image[pixA * 4 + 3];

    image[pixA * 4] = rB;
    image[pixA * 4 + 1] = gB;
    image[pixA * 4 + 2] = bB;
    image[pixA * 4 + 3] = aB;

    image[pixB * 4] = rA;
    image[pixB * 4 + 1] = gA;
    image[pixB * 4 + 2] = bA;
    image[pixB * 4 + 3] = aA;
}

// just the partition operation from quicksort
int GIF::GifPartition(uint8_t* image, const int left, const int right, const int elt, int pivotIndex) {
    const int pivotValue = image[(pivotIndex) * 4 + elt];
    GifSwapPixels(image, pivotIndex, right - 1);
    int storeIndex = left;
    bool split = 0;
    for (int ii = left; ii < right - 1; ++ii) {
        int arrayVal = image[ii * 4 + elt];
        if (arrayVal < pivotValue) {
            GifSwapPixels(image, ii, storeIndex);
            ++storeIndex;
        }
        else if (arrayVal == pivotValue) {
            if (split) {
                GifSwapPixels(image, ii, storeIndex);
                ++storeIndex;
            }
            split = !split;
        }
    }
    GifSwapPixels(image, storeIndex, right - 1);
    return storeIndex;
}

// Perform an incomplete sort, finding all elements above and below the desired median
void GIF::GifPartitionByMedian(uint8_t* image, int left, int right, int com, int neededCenter) {
    if (left < right - 1) {
        int pivotIndex = left + (right - left) / 2;

        pivotIndex = GifPartition(image, left, right, com, pivotIndex);

        // Only "sort" the section of the array that contains the median
        if (pivotIndex > neededCenter)
            GifPartitionByMedian(image, left, pivotIndex, com, neededCenter);

        if (pivotIndex < neededCenter)
            GifPartitionByMedian(image, pivotIndex + 1, right, com, neededCenter);
    }
}

// Builds a palette by creating a balanced k-d tree of all pixels in the image
void GIF::GifSplitPalette(uint8_t* image, int numPixels, int firstElt, int lastElt, int splitElt, int splitDist, int treeNode, bool buildForDither) {
    if (lastElt <= firstElt || numPixels == 0)
        return;

    // base case, bottom of the tree
    if (lastElt == firstElt + 1) {
        if (buildForDither) {
            // Dithering needs at least one color as dark as anything
            // in the image and at least one brightest color -
            // otherwise it builds up error and produces strange artifacts
            if (firstElt == 1) {
                // special case: the darkest color in the image
                uint32_t r = 255, g = 255, b = 255;
                for (int ii = 0; ii < numPixels; ++ii) {
                    r = (uint32_t)GifIMin((int32_t)r, image[ii * 4 + 0]);
                    g = (uint32_t)GifIMin((int32_t)g, image[ii * 4 + 1]);
                    b = (uint32_t)GifIMin((int32_t)b, image[ii * 4 + 2]);
                }

                palette.r[firstElt] = (uint8_t)r;
                palette.g[firstElt] = (uint8_t)g;
                palette.b[firstElt] = (uint8_t)b;

                return;
            }

            if (firstElt == (1 << palette.bitDepth) - 1) {
                // special case: the lightest color in the image
                uint32_t r = 0, g = 0, b = 0;
                for (int ii = 0; ii < numPixels; ++ii) {
                    r = (uint32_t)GifIMax((int32_t)r, image[ii * 4 + 0]);
                    g = (uint32_t)GifIMax((int32_t)g, image[ii * 4 + 1]);
                    b = (uint32_t)GifIMax((int32_t)b, image[ii * 4 + 2]);
                }

                palette.r[firstElt] = (uint8_t)r;
                palette.g[firstElt] = (uint8_t)g;
                palette.b[firstElt] = (uint8_t)b;

                return;
            }
        }

        // otherwise, take the average of all colors in this subcube
        uint64_t r = 0, g = 0, b = 0;
        for (int ii = 0; ii < numPixels; ++ii) {
            r += image[ii * 4 + 0];
            g += image[ii * 4 + 1];
            b += image[ii * 4 + 2];
        }

        r += (uint64_t)numPixels / 2;  // round to nearest
        g += (uint64_t)numPixels / 2;
        b += (uint64_t)numPixels / 2;

        r /= (uint64_t)numPixels;
        g /= (uint64_t)numPixels;
        b /= (uint64_t)numPixels;

        palette.r[firstElt] = (uint8_t)r;
        palette.g[firstElt] = (uint8_t)g;
        palette.b[firstElt] = (uint8_t)b;

        return;
    }

    // Find the axis with the largest range
    int minR = 255, maxR = 0;
    int minG = 255, maxG = 0;
    int minB = 255, maxB = 0;
    for (int ii = 0; ii < numPixels; ++ii)
    {
        int r = image[ii * 4 + 0];
        int g = image[ii * 4 + 1];
        int b = image[ii * 4 + 2];

        if (r > maxR) maxR = r;
        if (r < minR) minR = r;

        if (g > maxG) maxG = g;
        if (g < minG) minG = g;

        if (b > maxB) maxB = b;
        if (b < minB) minB = b;
    }

    int rRange = maxR - minR;
    int gRange = maxG - minG;
    int bRange = maxB - minB;

    // and split along that axis. (incidentally, this means this isn't a "proper" k-d tree but I don't know what else to call it)
    int splitCom = 1;
    if (bRange > gRange) splitCom = 2;
    if (rRange > bRange&& rRange > gRange) splitCom = 0;

    int subPixelsA = numPixels * (splitElt - firstElt) / (lastElt - firstElt);
    int subPixelsB = numPixels - subPixelsA;

    GifPartitionByMedian(image, 0, numPixels, splitCom, subPixelsA);

    palette.treeSplitElt[treeNode] = (uint8_t)splitCom;
    palette.treeSplit[treeNode] = image[subPixelsA * 4 + splitCom];

    GifSplitPalette(image, subPixelsA, firstElt, splitElt, splitElt - splitDist, splitDist / 2, treeNode * 2, buildForDither);
    GifSplitPalette(image + subPixelsA * 4, subPixelsB, splitElt, lastElt, splitElt + splitDist, splitDist / 2, treeNode * 2 + 1, buildForDither);
}

// Finds all pixels that have changed from the previous image and
// moves them to the fromt of th buffer.
// This allows us to build a palette optimized for the colors of the
// changed pixels only.
int GIF::GifPickChangedPixels(const uint8_t* lastFrame, uint8_t* frame, int numPixels) {
    int numChanged = 0;
    uint8_t* writeIter = frame;

    for (int ii = 0; ii < numPixels; ++ii) {
        if (lastFrame[0] != frame[0] ||
            lastFrame[1] != frame[1] ||
            lastFrame[2] != frame[2]) {
            writeIter[0] = frame[0];
            writeIter[1] = frame[1];
            writeIter[2] = frame[2];
            ++numChanged;
            writeIter += 4;
        }
        lastFrame += 4;
        frame += 4;
    }

    return numChanged;
}

// Creates a palette by placing all the image pixels in a k-d tree and then averaging the blocks at the bottom.
// This is known as the "modified median split" technique
void GIF::GifMakePalette(const uint8_t* lastFrame, const uint8_t* nextFrame, int bitDepth, bool buildForDither) {
    palette.bitDepth = bitDepth;

    // SplitPalette is destructive (it sorts the pixels by color) so
    // we must create a copy of the image for it to destroy
    size_t imageSize = (size_t)(width * height * 4 * sizeof(uint8_t));
    uint8_t* destroyableImage = (uint8_t*)GIF_TEMP_MALLOC(imageSize);
    memcpy(destroyableImage, nextFrame, imageSize);

    int numPixels = (int)(width * height);
    if (lastFrame)
        numPixels = GifPickChangedPixels(lastFrame, destroyableImage, numPixels);

    const int lastElt = 1 << bitDepth;
    const int splitElt = lastElt / 2;
    const int splitDist = splitElt / 2;

    GifSplitPalette(destroyableImage, numPixels, 1, lastElt, splitElt, splitDist, 1, buildForDither);

    GIF_TEMP_FREE(destroyableImage);

    // add the bottom node for the transparency index
    palette.treeSplit[1 << (bitDepth - 1)] = 0;
    palette.treeSplitElt[1 << (bitDepth - 1)] = 0;

    palette.r[0] = palette.g[0] = palette.b[0] = 0;
}

// Implements Floyd-Steinberg dithering, writes palette value to alpha
void GIF::GifDitherImage(const uint8_t* lastFrame, const uint8_t* nextFrame, uint8_t* outFrame)
{
    int numPixels = (int)(width * height);

    // quantPixels initially holds color*256 for all pixels
    // The extra 8 bits of precision allow for sub-single-color error values
    // to be propagated
    int32_t* quantPixels = (int32_t*)GIF_TEMP_MALLOC(sizeof(int32_t) * (size_t)numPixels * 4);

    for (int ii = 0; ii < numPixels * 4; ++ii) {
        uint8_t pix = nextFrame[ii];
        int32_t pix16 = int32_t(pix) * 256;
        quantPixels[ii] = pix16;
    }

    for (uint32_t yy = 0; yy < height; ++yy) {
        for (uint32_t xx = 0; xx < width; ++xx) {
            int32_t* nextPix = quantPixels + 4 * (yy * width + xx);
            const uint8_t* lastPix = lastFrame ? lastFrame + 4 * (yy * width + xx) : NULL;

            // Compute the colors we want (rounding to nearest)
            int32_t rr = (nextPix[0] + 127) / 256;
            int32_t gg = (nextPix[1] + 127) / 256;
            int32_t bb = (nextPix[2] + 127) / 256;

            // if it happens that we want the color from last frame, then just write out
            // a transparent pixel
            if (lastFrame &&
                lastPix[0] == rr &&
                lastPix[1] == gg &&
                lastPix[2] == bb) {
                nextPix[0] = rr;
                nextPix[1] = gg;
                nextPix[2] = bb;
                nextPix[3] = kGifTransIndex;
                continue;
            }

            int32_t bestDiff = 1000000;
            int32_t bestInd = kGifTransIndex;

            // Search the palete
            GifGetClosestPaletteColor(rr, gg, bb, bestInd, bestDiff);

            // Write the result to the temp buffer
            int32_t r_err = nextPix[0] - int32_t(palette.r[bestInd]) * 256;
            int32_t g_err = nextPix[1] - int32_t(palette.g[bestInd]) * 256;
            int32_t b_err = nextPix[2] - int32_t(palette.b[bestInd]) * 256;

            nextPix[0] = palette.r[bestInd];
            nextPix[1] = palette.g[bestInd];
            nextPix[2] = palette.b[bestInd];
            nextPix[3] = bestInd;

            // Propagate the error to the four adjacent locations
            // that we haven't touched yet
            int quantloc_7 = (int)(yy * width + xx + 1);
            int quantloc_3 = (int)(yy * width + width + xx - 1);
            int quantloc_5 = (int)(yy * width + width + xx);
            int quantloc_1 = (int)(yy * width + width + xx + 1);

            if (quantloc_7 < numPixels) {
                int32_t* pix7 = quantPixels + 4 * quantloc_7;
                pix7[0] += GifIMax(-pix7[0], r_err * 7 / 16);
                pix7[1] += GifIMax(-pix7[1], g_err * 7 / 16);
                pix7[2] += GifIMax(-pix7[2], b_err * 7 / 16);
            }

            if (quantloc_3 < numPixels) {
                int32_t* pix3 = quantPixels + 4 * quantloc_3;
                pix3[0] += GifIMax(-pix3[0], r_err * 3 / 16);
                pix3[1] += GifIMax(-pix3[1], g_err * 3 / 16);
                pix3[2] += GifIMax(-pix3[2], b_err * 3 / 16);
            }

            if (quantloc_5 < numPixels) {
                int32_t* pix5 = quantPixels + 4 * quantloc_5;
                pix5[0] += GifIMax(-pix5[0], r_err * 5 / 16);
                pix5[1] += GifIMax(-pix5[1], g_err * 5 / 16);
                pix5[2] += GifIMax(-pix5[2], b_err * 5 / 16);
            }

            if (quantloc_1 < numPixels) {
                int32_t* pix1 = quantPixels + 4 * quantloc_1;
                pix1[0] += GifIMax(-pix1[0], r_err / 16);
                pix1[1] += GifIMax(-pix1[1], g_err / 16);
                pix1[2] += GifIMax(-pix1[2], b_err / 16);
            }
        }
    }

    // Copy the palettized result to the output buffer
    for (int ii = 0; ii < numPixels * 4; ++ii)
    {
        outFrame[ii] = (uint8_t)quantPixels[ii];
    }

    GIF_TEMP_FREE(quantPixels);
}

// Picks palette colors for the image using simple thresholding, no dithering
void GIF::GifThresholdImage(const uint8_t* lastFrame, const uint8_t* nextFrame, uint8_t* outFrame) {
    uint32_t numPixels = width * height;
    for (uint32_t ii = 0; ii < numPixels; ++ii) {
        // if a previous color is available, and it matches the current color,
        // set the pixel to transparent
        if (lastFrame &&
            lastFrame[0] == nextFrame[0] &&
            lastFrame[1] == nextFrame[1] &&
            lastFrame[2] == nextFrame[2]) {
            outFrame[0] = lastFrame[0];
            outFrame[1] = lastFrame[1];
            outFrame[2] = lastFrame[2];
            outFrame[3] = kGifTransIndex;
        } else {
            // palettize the pixel
            int32_t bestDiff = 1000000;
            int32_t bestInd = 1;
            GifGetClosestPaletteColor(nextFrame[0], nextFrame[1], nextFrame[2], bestInd, bestDiff);

            // Write the resulting color to the output buffer
            outFrame[0] = palette.r[bestInd];
            outFrame[1] = palette.g[bestInd];
            outFrame[2] = palette.b[bestInd];
            outFrame[3] = (uint8_t)bestInd;
        }

        if (lastFrame) lastFrame += 4;
        outFrame += 4;
        nextFrame += 4;
    }
}

// insert a single bit
void GIF::GifWriteBit(uint32_t bit) {
    bit = bit & 1;
    bit = bit << bitStatus.bitIndex;
    bitStatus.byte |= bit;

    ++bitStatus.bitIndex;
    if (bitStatus.bitIndex > 7) {
        // move the newly-finished byte to the chunk buffer
        bitStatus.chunk[bitStatus.chunkIndex++] = bitStatus.byte;
        // and start a new byte
        bitStatus.bitIndex = 0;
        bitStatus.byte = 0;
    }
}

// write all bytes so far to the file
void GIF::GifWriteChunk(FILE* f) {
    fputc((int)bitStatus.chunkIndex, f);
    fwrite(bitStatus.chunk, 1, bitStatus.chunkIndex, f);

    bitStatus.bitIndex = 0;
    bitStatus.byte = 0;
    bitStatus.chunkIndex = 0;
}

void GIF::GifWriteCode(FILE* f, uint32_t code, uint32_t length) {
    for (uint32_t ii = 0; ii < length; ++ii) {
        GifWriteBit(code);
        code = code >> 1;

        if (bitStatus.chunkIndex == 255) {
            GifWriteChunk(f);
        }
    }
}

// write a 256-color (8-bit) image palette to the file
void GIF::GifWritePalette(FILE* f) {
    fputc(0, f);  // first color: transparency
    fputc(0, f);
    fputc(0, f);

    for (int ii = 1; ii < (1 << palette.bitDepth); ++ii) {
        uint32_t r = palette.r[ii];
        uint32_t g = palette.g[ii];
        uint32_t b = palette.b[ii];

        fputc((int)r, f);
        fputc((int)g, f);
        fputc((int)b, f);
    }
}

// write the image header, LZW-compress and write out the image
void GIF::GifWriteLzwImage(FILE* f, uint8_t* image, uint32_t left, uint32_t top, uint32_t delay) {
    // graphics control extension
    fputc(0x21, f);
    fputc(0xf9, f);
    fputc(0x04, f);
    fputc(0x05, f); // leave prev frame in place, this frame has transparency
    fputc(delay & 0xff, f);
    fputc((delay >> 8) & 0xff, f);
    fputc(kGifTransIndex, f); // transparent color index
    fputc(0, f);

    fputc(0x2c, f); // image descriptor block

    fputc(left & 0xff, f);           // corner of image in canvas space
    fputc((left >> 8) & 0xff, f);
    fputc(top & 0xff, f);
    fputc((top >> 8) & 0xff, f);

    fputc(width & 0xff, f);          // width and height of image
    fputc((width >> 8) & 0xff, f);
    fputc(height & 0xff, f);
    fputc((height >> 8) & 0xff, f);

    //fputc(0, f); // no local color table, no transparency
    //fputc(0x80, f); // no local color table, but transparency

    fputc(0x80 + palette.bitDepth - 1, f); // local color table present, 2 ^ bitDepth entries
    GifWritePalette(f);

    const int minCodeSize = palette.bitDepth;
    const uint32_t clearCode = 1 << palette.bitDepth;

    fputc(minCodeSize, f); // min code size 8 bits

    GifLzwNode* codetree = (GifLzwNode*)GIF_TEMP_MALLOC(sizeof(GifLzwNode) * 4096);

    memset(codetree, 0, sizeof(GifLzwNode) * 4096);
    int32_t curCode = -1;
    uint32_t codeSize = (uint32_t)minCodeSize + 1;
    uint32_t maxCode = clearCode + 1;

    bitStatus.byte = 0;
    bitStatus.bitIndex = 0;
    bitStatus.chunkIndex = 0;

    GifWriteCode(f, clearCode, codeSize);  // start with a fresh LZW dictionary

    for (uint32_t yy = 0; yy < height; ++yy) {
        for (uint32_t xx = 0; xx < width; ++xx) {
#ifdef GIF_FLIP_VERT
            // bottom-left origin image (such as an OpenGL capture)
            uint8_t nextValue = image[((height - 1 - yy) * width + xx) * 4 + 3];
#else
            // top-left origin
            uint8_t nextValue = image[(yy * width + xx) * 4 + 3];
#endif

            // "loser mode" - no compression, every single code is followed immediately by a clear
            //WriteCode( f, stat, nextValue, codeSize );
            //WriteCode( f, stat, 256, codeSize );

            if (curCode < 0) {
                // first value in a new run
                curCode = nextValue;
            }
            else if (codetree[curCode].m_next[nextValue]) {
                // current run already in the dictionary
                curCode = codetree[curCode].m_next[nextValue];
            }
            else {
                // finish the current run, write a code
                GifWriteCode(f, (uint32_t)curCode, codeSize);

                // insert the new run into the dictionary
                codetree[curCode].m_next[nextValue] = (uint16_t)++maxCode;

                if (maxCode >= (1ul << codeSize)) {
                    // dictionary entry count has broken a size barrier,
                    // we need more bits for codes
                    codeSize++;
                }
                if (maxCode == 4095) {
                    // the dictionary is full, clear it out and begin anew
                    GifWriteCode(f, clearCode, codeSize); // clear tree

                    memset(codetree, 0, sizeof(GifLzwNode) * 4096);
                    codeSize = (uint32_t)(minCodeSize + 1);
                    maxCode = clearCode + 1;
                }

                curCode = nextValue;
            }
        }
    }

    // compression footer
    GifWriteCode(f, (uint32_t)curCode, codeSize);
    GifWriteCode(f, clearCode, codeSize);
    GifWriteCode(f, clearCode + 1, (uint32_t)minCodeSize + 1);

    // write out the last partial chunk
    while (bitStatus.bitIndex) GifWriteBit(0);
    if (bitStatus.chunkIndex) GifWriteChunk(f);

    fputc(0, f); // image block terminator

    GIF_TEMP_FREE(codetree);
}

// Creates a gif file.
// The input GIFWriter is assumed to be uninitialized.
// The delay value is the time between frames in hundredths of a second - note that not all viewers pay much attention to this value.
bool GIF::GifBegin(const char* filename, uint32_t delay, int32_t bitDepth, bool dither) {
    (void)bitDepth; (void)dither; // Mute "Unused argument" warnings
#if defined(_MSC_VER) && (_MSC_VER >= 1400)
    writer->f = 0;
    fopen_s(&writer->f, filename, "wb");
#else
    writer->f = fopen(filename, "wb");
#endif
    if (!writer->f) return false;

    writer->firstFrame = true;

    // allocate
    writer->oldImage = (uint8_t*)GIF_MALLOC(width * height * 4);

    fputs("GIF89a", writer->f);

    // screen descriptor
    fputc(width & 0xff, writer->f);
    fputc((width >> 8) & 0xff, writer->f);
    fputc(height & 0xff, writer->f);
    fputc((height >> 8) & 0xff, writer->f);

    fputc(0xf0, writer->f);  // there is an unsorted global color table of 2 entries
    fputc(0, writer->f);     // background color
    fputc(0, writer->f);     // pixels are square (we need to specify this because it's 1989)

    // now the "global" palette (really just a dummy palette)
    // color 0: black
    fputc(0, writer->f);
    fputc(0, writer->f);
    fputc(0, writer->f);
    // color 1: also black
    fputc(0, writer->f);
    fputc(0, writer->f);
    fputc(0, writer->f);

    if (delay != 0) {
        // animation header
        fputc(0x21, writer->f); // extension
        fputc(0xff, writer->f); // application specific
        fputc(11, writer->f); // length 11
        fputs("NETSCAPE2.0", writer->f); // yes, really
        fputc(3, writer->f); // 3 bytes of NETSCAPE2.0 data

        fputc(1, writer->f); // JUST BECAUSE
        fputc(0, writer->f); // loop infinitely (byte 0)
        fputc(0, writer->f); // loop infinitely (byte 1)

        fputc(0, writer->f); // block terminator
    }

    return true;
}

// Writes the EOF code, closes the file handle, and frees temp memory used by a GIF.
// Many if not most viewers will still display a GIF properly if the EOF code is missing,
// but it's still a good idea to write it out.
bool GIF::GifEnd()
{
    if (!writer->f) return false;

    fputc(0x3b, writer->f); // end of file
    fclose(writer->f);
    GIF_FREE(writer->oldImage);

    writer->f = NULL;
    writer->oldImage = NULL;

    return true;
}

// Writes out a new frame to a GIF in progress.
// The GIFWriter should have been created by GIFBegin.
// AFAIK, it is legal to use different bit depths for different frames of an image -
// this may be handy to save bits in animations that don't change much.
bool GIF::GifWriteFrame(const uint8_t* image, uint32_t delay, int bitDepth, bool dither) {
    if (!writer->f) return false;

    const uint8_t* oldImage = writer->firstFrame ? NULL : writer->oldImage;
    writer->firstFrame = false;

    GifMakePalette((dither ? NULL : oldImage), image, bitDepth, dither);

    if (dither)
        GifDitherImage(oldImage, image, writer->oldImage);
    else
        GifThresholdImage(oldImage, image, writer->oldImage);

    GifWriteLzwImage(writer->f, writer->oldImage, 0, 0, delay);

    return true;
}