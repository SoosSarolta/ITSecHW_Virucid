import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  currentCaff: Caff;
  comments: Array<string>;
  commentText: string = '';

  constructor(
    private _router: ActivatedRoute,
    private _network: NetworkService,
    private _sanitization: DomSanitizer
  ) {
  }

  ngOnInit(): void {
    this._router.queryParams.subscribe(params => {
      console.log(params['id']);
      this._network.details(params['id']).then(data => {
        console.log(data);
        var url = 'data:image/GIF;base64,' + encodeURIComponent(data.gifFile);
        var image = this._sanitization.bypassSecurityTrustResourceUrl(url);
        this.currentCaff = new Caff(data.id, data.originalFileName, image);
        this.comments = new Array();
        data.comments.forEach(element => {
          console.log("comment: ", element.comment);
          this.comments.push(element.comment);
        });
      }).catch(err => {
        console.log(err);
      });
    });
  }

  downloadCaff() {
    console.log("downloading caff...");
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
