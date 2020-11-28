import { Component, Inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FileSystemDirectoryEntry, FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';
import { AuthService } from 'src/app/service/auth/auth.service';
import { DomSanitizer } from '@angular/platform-browser';

export interface DialogData {
  message: string;
}

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  public caffFiles: NgxFileDropEntry[] = [];
  caffData: FormData;
  allowedFileType: string;
  caffs: Array<Caff>;
  userId: string;
  token: string;
  isValidFile: boolean;
  isUploading: boolean = true;

  constructor(
    private _sanitization: DomSanitizer,
    private _network: NetworkService,
    private _router: Router,
    private _auth: AuthService
  ) { }

  ngOnInit(): void {
    this.caffData = new FormData();
    this.allowedFileType = '.caff';
    this.caffs = [];
    this.userId = localStorage.getItem('user_id');
    this.token = localStorage.getItem('token');
    this.isValidFile = false;
    this._network.home().then(data => {
      data.forEach(element => {
        const url = 'data:image/JPEG;base64,' + encodeURIComponent(element.bitmapFile);
        const image = this._sanitization.bypassSecurityTrustResourceUrl(url);
        this.caffs.push(new Caff(element.id, element.originalFileName, image));
        console.log('this.caffs: ', this.caffs);
      });
    }).catch(err => {
      console.log(err);
    });
  }

  public dropped(files: NgxFileDropEntry[]): void {
    this.caffFiles = files;
    for (const droppedFile of files) {

      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          if (droppedFile.fileEntry.name.includes(this.allowedFileType) && files.length === 1) {
            this.caffData = new FormData();
            this.caffData.append('file', file);
            this.isValidFile = true;
          } else {
            this.isValidFile = false;
          }
        });
      } else {
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
      }
    }
  }

  navigateToDetails(id: string): void {
    this._router.navigate(['/' + RouterPath.detail], { queryParams: { id } });
  }

  public uploadCaff(): void {
    if (this.isValidFile) {
      this.isUploading = false;
      this._network.uploadCaff(this.userId, this.caffData).then(data => {
        alert('File uploaded!');
        this.caffFiles = [];
        window.location.reload();
      }).catch(err => {
        alert('Uploading failed!');
        window.location.reload();
      });
    }
  }

  public fileOver(event): void {}

  public fileLeave(event): void {}

  get isAdmin(): boolean {
    return this._auth.hasRole('ADMIN');
  }
}
