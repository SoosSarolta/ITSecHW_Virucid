import { SecurityContext } from '@angular/compiler/src/core';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { FileSaverService } from 'ngx-filesaver';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';
import * as FileSaver from 'file-saver';

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
    private _router: Router,
    private _activatedRoute: ActivatedRoute,
    private _network: NetworkService,
    private _sanitization: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this._activatedRoute.queryParams.subscribe(params => {
      this._network.details(params.id).then(data => {
        const url = 'data:image/GIF;base64,' + encodeURIComponent(data.gifFile);
        const image = this._sanitization.bypassSecurityTrustResourceUrl(url);
        this.currentCaff = new Caff(data.id, data.originalFileName, image);
        this.comments = new Map();
        data.comments.forEach(element => {
          this.comments.set(element.timeStamp, element.comment);
        });
      }).catch(err => {
        console.log(err);
        switch (err.status) {
          case 400:
            this._router.navigate(['error', 'bad-request']);
            break;
          case 403:
            this._router.navigate(['error', 'forbidden']);
            break;
          case 404:
            this._router.navigate(['error', 'not-found']);
            break;
          default:
            this._router.navigate(['login']);
            break;
        }
      });
    });
  }

  downloadCaff(): void {
    this._network.downloadCaff(this.currentCaff.id).then(data => {
      const blob = new Blob([data], { type: 'application/zip' });
      FileSaver.saveAs(blob, data.originalFileName);
    }).catch(err => {
      console.log(err);
    });
  }

  comment(): void {
    this._network.addComment(localStorage.getItem('user_id'), this.currentCaff.id, this.commentText).then(data => {
      this.commentText = '';
      window.location.reload();
    }).catch(err => {
      console.log(err);
    });
  }
}
