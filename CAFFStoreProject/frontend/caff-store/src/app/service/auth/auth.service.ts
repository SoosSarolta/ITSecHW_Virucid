import { Injectable } from '@angular/core';
import { JwtModule, JwtHelperService } from '@auth0/angular-jwt';
import { User } from '../../model/user';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public jwtHelper: JwtHelperService = new JwtHelperService();

  constructor() { }

  public isAuthenticated(): boolean {
    // console.log('isAuthenticated called');
    const token = localStorage.getItem('token');

    return !this.jwtHelper.isTokenExpired(token);
  }

  public hasRole(r: string): boolean {
    // console.log('hasRole() called');
    return this.isAuthenticated() && localStorage.getItem('role') === r;
  }

  public logout(): void {
    // console.log('logout() called');
    localStorage.removeItem('token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('role');
  }

  public login(id: string, token: string, role: string): void {
    localStorage.setItem('token', token.split(' ')[1]);
    localStorage.setItem('user_id', id);
    localStorage.setItem('role', role);  }
}
