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

  private async loadUserInfo(): Promise<any> {
    this._network.profile(this.userId).then(data => {
      this.username = data.username;
      this.email = data.email;
      this.comments = [];
      data.comments.forEach(element => {
        this.comments.push(element.comment);
      });
      this.caffFiles = [];
      data.caffFilesWithoutBitmap.forEach(element => {
        this.caffFiles.push(element);
      });
    });
  }

  async updateUsername(): Promise<any> {
    this._network.updateUsername(this.userId, this.username).then(data => {
      console.log(data);
    }).catch(err => {
      console.log(err);
    });
  }

  navigateToDetails(id: string): void {
    this._router.navigate([RouterPath.detail], { queryParams: { id } });
  }

  get isAdmin(): boolean {
    return this._auth.hasRole('ADMIN');
  }
}
