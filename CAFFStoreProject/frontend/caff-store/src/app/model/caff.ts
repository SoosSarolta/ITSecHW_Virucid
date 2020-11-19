import { SafeResourceUrl } from '@angular/platform-browser';

export class Caff {
  
    id: string;
    fileName: string;
    bitmapFileName: SafeResourceUrl
  
    constructor(id: string, fileName: string, bitmapFileName: SafeResourceUrl) {
        this.id = id;
        this.fileName = fileName;
        this.bitmapFileName = bitmapFileName;
    }
}