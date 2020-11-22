import { Component } from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from './service/auth/auth.service';
import {RouterPath} from "./util/router-path";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'caff-store';

  constructor(
    private _router: Router,
    private _authService: AuthService) { }

  get isAuthenticated() {
    return this._authService.isAuthenticated();
  }
  get isAdmin() {
    return this._authService.hasRole('ADMIN');
  }
  logout() {
    this._authService.logout();
    this._router.navigate(['login']);
  }

  navigateToProfile(): void {
    this._router.navigate(['/' + RouterPath.profil], { queryParams: { id: localStorage.getItem('user_id') } });
  }
}

