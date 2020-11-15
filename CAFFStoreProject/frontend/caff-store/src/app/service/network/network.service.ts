import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from 'src/app/model/user';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  serverAddress: string = "http://localhost:8080/";
  loginURL: string = "login";
  registerURL: string = "register";
  homeURL: string = "main";

  headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor(private _http: HttpClient) { }

  register(user: User): Promise<any> {
    var json = JSON.stringify(user);
    return this.postJSON(this.serverAddress, this.registerURL, json);
  }

  login(email: string, password: string): Promise<any> {
    return this.getJSON(this.serverAddress, this.loginURL + '?email=' + email + '&password=' + password);
  }

  home(): Promise<any> {
    return this.getJSON(this.serverAddress, this.homeURL);
  }

  private async postJSON(address: string, url: string, json: string): Promise<any> {
    const response = await this._http.post(address + url, json, { headers: this.headers }).toPromise();
    return response;
  }

  private async getJSON(address: string, url: string) {
    const response = await this._http.get(address + url).toPromise();
    return response;
  }
}
