import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
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
    private _auth: AuthService
  ) { }

  ngOnInit(): void {
    // this._router.queryParams.subscribe(params => {
    //   console.log(params['id']);
    //   this._network.profile(params['id']).then(data => {
    //     console.log(data);
    //   }).catch(err => {
    //     console.log(err);
    //   });
    // });
    this.userId = localStorage.getItem("user_id");
    this.loadUserInfo();
  }

  private async loadUserInfo() {
    this._network.profile(this.userId).then(data => {
      this.username = data["username"];
      this.email = data["email"];
      this.comments = data["comments"];
      this.caffFiles = data["caffFilesWithoutBitmap"];
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

}
