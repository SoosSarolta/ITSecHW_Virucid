import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import { AuthService } from 'src/app/service/auth/auth.service';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  userId: string;
  username: string;
  email: string;
  comments: Array<string>;
  caffFiles: Array<string>;

  constructor(
    private _router: Router,
    private _network: NetworkService,
    private _auth: AuthService,
    private _route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this._route.queryParams.subscribe(params => {
      this.userId = params.id;
    });

    this.loadUserInfo();
  }

  private async loadUserInfo() {
    this._network.profile(this.userId).then(data => {
      this.username = data["username"];
      this.email = data["email"];
      this.comments = new Array();
      data.comments.forEach(element => {
        this.comments.push(element.comment);
      });
      this.caffFiles = new Array();
      data.caffFilesWithoutBitmap.forEach(element => {
        this.caffFiles.push(element)
      })
    });
  }

  async updateUsername() {
    this._network.updateUsername(this.userId, this.username).then(data => {
      console.log(data);
    }).catch(err => {
      console.log(err);
    });
  }

  logout() {
    this._auth.logout();
    this._router.navigate(['/' + RouterPath.login]);
  }

  navigateToDetails(id: string) {
    this._router.navigate(['/' + RouterPath.detail], { queryParams: { id: id } });
  }
}
