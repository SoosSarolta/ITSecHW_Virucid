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
  // localhost:8080/caffs/parse?filename=1.caff
  uploadCaffURL: string = "caffs/parse";

  headers = new HttpHeaders({
    'Content-Type': 'application/json'
  });

  constructor(private _http: HttpClient) { }

  register(user: User): Promise<any> {
    var json = JSON.stringify({
      "personName": user.personName,
      "email": user.email,
      "password": user.password
    });
    return this.postJSON(this.serverAddress, this.registerURL, json);
  }

  login(email: string, password: string): Promise<any> {
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

  uploadCaff(fileName: string, formData: FormData): Promise<any> {
    return this.postFile(this.serverAddress, this.uploadCaffURL + '?filename=' + fileName, formData);
  }

  private async postJSON(address: string, url: string, json: string): Promise<any> {
    const response = await this._http.post(address + url, json, { headers: this.headers }).toPromise();
    return response;
  }

  private async getJSON(address: string, url: string) {
    const response = await this._http.get(address + url).toPromise();
    return response;
  }

  private async postFile(address: string, url: string, formData: FormData): Promise<any> {
    const respone = await this._http.post(address + url, formData, { responseType: 'json' }).toPromise();
    return respone;
  }
}
