import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  serverAddress: string = "http://localhost:8080/";
  loginURL: string = "login";
  registerURL: string = "register";

  headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor(private _http: HttpClient) { }

  register(name: string, email: string, password: string): Promise<any> {
    return this.getJSON(this.serverAddress, this.registerURL + '?name=' + name + '&email=' + email + '&password=' + password);
  }

  login(email: string, password: string): Promise<any> {
    return this.getJSON(this.serverAddress, this.registerURL + '?email=' + email + '&password=' + password);
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
