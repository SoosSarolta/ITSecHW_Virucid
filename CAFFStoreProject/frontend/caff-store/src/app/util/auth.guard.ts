import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth/auth.service';
import { NetworkService } from '../service/network/network.service';
import { RouterPath } from './router-path';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(
    private _router: Router,
    private _authService: AuthService,
    private _network: NetworkService
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (!this._authService.isAuthenticated()) {
      // TODO: Uncomment, when registration works
      // this._router.navigate(['/' + RouterPath.login]);
      // return false;
    }
    return true;
  }

}
