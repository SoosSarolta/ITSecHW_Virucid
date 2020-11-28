import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
  Route,
  CanLoad
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../service/auth/auth.service';
import { NetworkService } from '../service/network/network.service';
import { RouterPath } from './router-path';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate, CanLoad {

  constructor(
    private _router: Router,
    private _authService: AuthService,
    private _network: NetworkService
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
    console.log('canActivate called.');
    if (!this._authService.isAuthenticated()) {
      this._router.navigate([RouterPath.login]);
      return false;
    }
    const roles = route.data.roles as string[];
    console.log(route.data);
    if (roles && !roles.some(r => this._authService.hasRole(r))) {
      this._router.navigate(['error', 'not-found']);
      return false;
    }
    return true;
  }

  canLoad(route: Route): Observable<boolean> | Promise<boolean> | boolean {
    console.log('canLoad called.');
    if (!this._authService.isAuthenticated()) {
      return false;
    }
    const roles = route.data && route.data.roles as string[];
    if (roles && !roles.some(r => this._authService.hasRole(r))) {
      return false;
    }
    return true;
  }
}
