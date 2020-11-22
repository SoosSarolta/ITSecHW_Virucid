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

  constructor(
    private _sanitization: DomSanitizer,
    private _network: NetworkService,
    private _router: Router,
    private _auth: AuthService
  ) { }

  ngOnInit(): void {
    this.caffData = new FormData();
    this.allowedFileType = ".caff";
    this.caffs = new Array();
    this.userId = localStorage.getItem("user_id");
    this.token = localStorage.getItem("token");
    console.log(this.token);
    this.isValidFile = false;
    this._network.home().then(data => {
      console.log("home: ", data);
      data.forEach(element => {
        var url = 'data:image/JPEG;base64,' + encodeURIComponent(element.bitmapFile);
        var image = this._sanitization.bypassSecurityTrustResourceUrl(url);
        this.caffs.push(new Caff(element.id, element.originalFileName, image));
        console.log("this.caffs: ", this.caffs);
      });
    }).catch(err => {
      console.log(err);
    })
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.caffFiles = files;
    for (const droppedFile of files) {

      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          if (droppedFile.fileEntry.name.includes(this.allowedFileType) && files.length == 1) {
            this.caffData = new FormData();
            console.log("file: ", file);
            console.log("droppedFile.relativePath: ", droppedFile.relativePath);
            this.caffData.append('file', file);
            this.isValidFile = true;
          } else {
            this.isValidFile = false;
          }
        });
      } else {
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  navigateToDetails(id: string) {
    this._router.navigate(['/' + RouterPath.detail], { queryParams: { id: id } });
  }

  navigateToProfile() {
    this._router.navigate(['/' + RouterPath.profil], { queryParams: { id: localStorage.getItem('user_id') } });
  }

  public uploadCaff() {
    if (this.isValidFile) {
      console.log("this.caffData: ", this.caffData);
      this._network.uploadCaff(this.userId, this.caffData).then(data => {
        alert("File uploaded!");
        this.caffFiles = [];
        window.location.reload();
      }).catch(err => {
        console.log(err);
      });
    }
  }

  public fileOver(event) {
    console.log(event);
  }

  public fileLeave(event) {
    console.log(event);
  }

  logout() {
    this._auth.logout();
    this._router.navigate(['/' + RouterPath.login]);
  }

}
