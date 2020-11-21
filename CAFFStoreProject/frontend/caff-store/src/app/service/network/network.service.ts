import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {User} from 'src/app/model/user';

@Injectable({
  providedIn: 'root'
})
export class NetworkService {
  serverAddress = 'http://localhost:8080/';
  // localhost:8080/login?email=kurdi.boti@gmail.com&password=mypassword
  loginURL = 'login';
  registerURL = 'register';
  caffURL = 'caffs';
  profilURL = 'users';
  uploadCaffURL = 'caffs/upload';

  header = new HttpHeaders();

  constructor(private _http: HttpClient) { }


  // Main network functions

  register(user: User): Promise<any> {
    this.resetHeader();
    this.setContentTypeHeader('application/json');
    this.setResponseTypeHeader('json');
    const json = JSON.stringify({
      personName: user.personName,
      email: user.email,
      password: user.password
    });
    return this.postJSON(this.serverAddress, this.registerURL, json);
  }

  async login(email: string, password: string): Promise<any> {
    this.resetHeader();
    this.setResponseTypeHeader('json');
    return this.getJSON(this.serverAddress, this.loginURL + '?email=' + email + '&password=' + password);
  }

  home(): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setResponseTypeHeader('json');
    return this.getJSON(this.serverAddress, this.caffURL);
  }

  details(id: string): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setResponseTypeHeader('json');
    return this.getJSON(this.serverAddress, this.caffURL + '/' + id);
  }

  async profile(id: string): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setResponseTypeHeader('json');
    return this.getJSON(this.serverAddress, this.profilURL + '/' + id);
  }

  async updateUsername(id: string, username: string): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setResponseTypeHeader('json');
    return this.put(this.serverAddress, `${this.profilURL}/${id}?username=${username}`);
  }

  uploadCaff(userId: string, formData: FormData): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setContentTypeHeader('application/json');
    return this.postFile(this.serverAddress, this.uploadCaffURL + '?userId=' + userId, formData);
  }

  admin(): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setResponseTypeHeader('json');
    return this.getJSON(this.serverAddress, this.profilURL);
  }

  deleteUser(id: string): Promise<any> {
    this.resetHeader();
    this.addAuthHeader();
    this.setResponseTypeHeader('json');
    return this.delete(this.serverAddress, `${this.profilURL}/${id}`);
  }


  // Http-operations

  // GET
  private async getJSON(address: string, url: string): Promise<any> {
    return await this._http.get(address + url, {headers: this.header}).toPromise();
  }

  // POST
  private async postJSON(address: string, url: string, json: string): Promise<any> {
    return await this._http.post(address + url, json, {headers: this.header}).toPromise();
  }

  private async postFile(address: string, url: string, formData: FormData): Promise<any> {
    return await this._http.post(address + url, formData, {headers: this.header}).toPromise();
  }

  // PUT
  private async put(address: string, url: string): Promise<any> {
    return await this._http.put(`${address}${url}`, '', {headers: this.header}).toPromise();
  }

  // DELETE
  private async delete(address: string, url: string): Promise<any> {
    return await this._http.delete(address + url, {headers: this.header}).toPromise();
  }


  // Header-settings

  private addAuthHeader(): void {
    this.header = this.header.append('Authorization', `Bearer ${localStorage.getItem('token')}`);
  }

  private setResponseTypeHeader(responseType: string): void {
    this.header = this.header.append('responseType', responseType);
  }

  private setContentTypeHeader(contentType: string): void {
    this.header = this.header.append('Content-Type', contentType);
  }
  private resetHeader(): void {
    this.header = new HttpHeaders();
  }
}
