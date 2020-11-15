import { Injectable } from '@angular/core';
import { JwtModule, JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public jwtHelper: JwtHelperService = new JwtHelperService();

  constructor() { }

  public isAuthenticated(): boolean {
    const token = localStorage.getItem('token');

    return !this.jwtHelper.isTokenExpired(token);
  }

  public logout(): void {
    localStorage.removeItem('token');
  }
}
