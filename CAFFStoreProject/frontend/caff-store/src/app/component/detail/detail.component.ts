import { SecurityContext } from '@angular/compiler/src/core';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { FileSaverService } from 'ngx-filesaver';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';
import * as FileSaver from "file-saver";

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  currentCaff: Caff;
  comments: Map<string, string>;
  commentText: string = '';

  constructor(
    private _router: ActivatedRoute,
    private _network: NetworkService,
    private _sanitization: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this._router.queryParams.subscribe(params => {
      console.log("caff id: ", params['id']);
      console.log("token: ", localStorage.getItem('token'));
      this._network.details(params['id']).then(data => {
        console.log(data);
        var url = 'data:image/GIF;base64,' + encodeURIComponent(data.gifFile);
        var image = this._sanitization.bypassSecurityTrustResourceUrl(url);
        this.currentCaff = new Caff(data.id, data.originalFileName, image);
        this.comments = new Map();
        data.comments.forEach(element => {
          console.log("comment: ", element.comment + " - " + element.timeStamp);
          this.comments.set(element.timeStamp, element.comment);
        });
      }).catch(err => {
        console.log(err);
      });
    });
  }

  downloadCaff() {
    console.log("downloading caff...");
    this._network.downloadCaff(this.currentCaff.id).then(data => {
      console.log("downloaded data: ", data);
      console.log("caff is downloaded!");
      var blob = new Blob([data], { type: "application/zip" });
      FileSaver.saveAs(blob, data.originalFileName);
    }).catch(err => {
      console.log(err);
    });
  }

  comment() {
    console.log("comment: ", this.commentText);
    this._network.addComment(localStorage.getItem("user_id"), this.currentCaff.id, this.commentText).then(data => {
      console.log("network.addComment response: ", data);
      this.commentText = "";
      window.location.reload();
    }).catch(err => {
      console.log(err);
    });
  }
}
