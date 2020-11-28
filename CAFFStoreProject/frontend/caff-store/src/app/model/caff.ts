import { SafeResourceUrl } from '@angular/platform-browser';

export class Caff {

    id: string;
    originalFileName: string;
    bitmapFile: SafeResourceUrl;

    constructor(id: string, originalFileName: string, bitmapFile: SafeResourceUrl) {
        this.id = id;
        this.originalFileName = originalFileName;
        this.bitmapFile = bitmapFile;
    }
}
