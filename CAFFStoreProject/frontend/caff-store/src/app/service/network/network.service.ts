import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from 'src/app/model/user';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  serverAddress: string = "http://localhost:8080/";
  // localhost:8080/login?email=kurdi.boti@gmail.com&password=mypassword
  loginURL: string = "login";
  registerURL: string = "register";
  caffURL: string = "caffs"
  profilURL: string = "users"
  uploadCaffURL: string = "caffs/upload";

  headers = new HttpHeaders({
    'Content-Type': 'application/json',
    responseType: 'json',
    'Authorization': `Bearer ${localStorage.getItem('token')}`
  });

  multipartHeader = new HttpHeaders({
    'Authorization': `Bearer ${localStorage.getItem('token')}`,
    'Access-Control-Allow-Origin': "*",
    'Access-Control-Allow-Methods': 'GET, POST, PATCH, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Origin, Content-Type, X-Auth-Token'
  });

  noAuthHeader = new HttpHeaders({
    'Content-Type': 'application/json',
    responseType: 'json',
    'Access-Control-Allow-Origin': "*",
    'Access-Control-Allow-Methods': 'GET, POST, PATCH, PUT, DELETE, OPTIONS',
    'Access-Control-Allow-Headers': 'Origin, Content-Type, X-Auth-Token'
  });

  constructor(private _http: HttpClient) { }

  register(user: User): Promise<any> {
    var json = JSON.stringify({
      "personName": user.personName,
      "email": user.email,
      "password": user.password
    });
    return this.postWithoutAuthJSON(this.serverAddress, this.registerURL, json);
  }

  async login(email: string, password: string): Promise<any> {
    return this.getJSON(this.serverAddress, this.loginURL + '?email=' + email + '&password=' + password);
  }

  home(): Promise<any> {
    return this.getJSON(this.serverAddress, this.caffURL);
  }

  details(id: string): Promise<any> {
    return this.getJSON(this.serverAddress, this.caffURL + '/' + id);
  }

  profile(id: string): Promise<any> {
    return this.getJSON(this.serverAddress, this.profilURL + '/' + id);
  }

  uploadCaff(userId: string, formData: FormData): Promise<any> {
    return this.postFile(this.serverAddress, this.uploadCaffURL + '?userId=' + userId, formData);
  }

  private async postJSON(address: string, url: string, json: string): Promise<any> {
    const response = await this._http.post(address + url, json, { headers: this.headers }).toPromise();
    return response;
  }

  private async postWithoutAuthJSON(address: string, url: string, json: string): Promise<any> {
    const response = await this._http.post(address + url, json, { headers: this.noAuthHeader }).toPromise();
    return response;
  }

  private async getJSON(address: string, url: string) {
    const response = await this._http.get(address + url).toPromise();
    return response;
  }

  private async postFile(address: string, url: string, formData: FormData): Promise<any> {
    const respone = await this._http.post(address + url, formData, { headers: this.multipartHeader }).toPromise();
    return respone;
  }
}
