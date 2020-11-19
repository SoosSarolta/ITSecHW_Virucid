import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FileSystemDirectoryEntry, FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';
import { AuthService } from 'src/app/service/auth/auth.service';

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
    private _network: NetworkService,
    private _router: Router,
    private _auth: AuthService
  ) { }

  ngOnInit(): void {
    this.caffData = new FormData();
    this.allowedFileType = ".caff";
    this.caffs = new Array();
    for (let i = 0; i < 20; i++) {
      this.caffs.push(new Caff("jlknsdlfnfds", "kjdfnsdfskdjmnfs.caff"));
    }
    this.userId = localStorage.getItem("user_id");
    this.token = localStorage.getItem("token");
    console.log(this.token);
    this.isValidFile = false;
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.caffFiles = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          if (droppedFile.fileEntry.name.includes(this.allowedFileType) && files.length == 1) {
            // Here you can access the real file
            this.caffData = new FormData()
            this.caffData.append('file', file, droppedFile.relativePath);
            this.isValidFile = true;
          } else {
            this.isValidFile = false;
          }
        });
      } else {
        // It was a directory (empty directories are added, otherwise only files)
        const fileEntry = droppedFile.fileEntry as FileSystemDirectoryEntry;
        console.log(droppedFile.relativePath, fileEntry);
      }
    }
  }

  navigateToDetails(id: string) {
    this._router.navigate(['/' + RouterPath.detail], { queryParams: { id: id } });
  }

  navigateToProfile() {
    this._router.navigate(['/' + RouterPath.profil]);
  }

  public uploadCaff() {
    if (this.isValidFile) {
      console.log("this.caffData: ", this.caffData);
      this._network.uploadCaff(this.userId, this.caffData).then(data => {
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
