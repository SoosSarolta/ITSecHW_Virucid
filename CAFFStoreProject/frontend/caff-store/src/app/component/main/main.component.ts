import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FileSystemDirectoryEntry, FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';
import { AuthService} from 'src/app/service/auth/auth.service';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  public files: NgxFileDropEntry[] = [];
  caffData: FormData;
  allowedFileType: string;
  caffs: Array<Caff>;
  userId: string;

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
    this.userId = "ghjklsadf"; // TODO query current user's id
  }

  public dropped(files: NgxFileDropEntry[]) {
    this.files = files;
    for (const droppedFile of files) {

      // Is it a file?
      if (droppedFile.fileEntry.isFile) {
        const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
        fileEntry.file((file: File) => {
          if (droppedFile.fileEntry.name.includes(this.allowedFileType)) {
            // Here you can access the real file
            const formData = new FormData()
            formData.append('file', file, droppedFile.relativePath)

            // Headers TODO: AUTHORIZATION
            const headers = new HttpHeaders({
              'security-token': 'mytoken'
            });

            this._network.uploadCaff(this.userId, formData).then(data => {
            }).catch(err => {
              console.log(err);
            });
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

  // TODO : add id argument after valid login
  navigateToProfile(){
    //this._router.navigate(['/' + RouterPath.profil], { queryParams: { id: userId } });
  }

  public uploadCaff() {
    console.log("uploadCaff");
  }

  public fileOver(event) {
    console.log(event);
  }

  public fileLeave(event) {
    console.log(event);
  }

  logout() {
    this._auth.logout();
    this._router.navigate(['login']);
  }

}
