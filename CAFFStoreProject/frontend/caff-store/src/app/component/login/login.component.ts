import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidationErrors, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/model/user';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  hidePassword = true;
  showDetails: boolean = true;
  user: User;
  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  passwordFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    Validators.maxLength(30)
  ]);
  loginForm: FormGroup;
  emailRegex = '^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,20}$';

  constructor(
    private _formBuilder: FormBuilder,
    private _network: NetworkService,
    private _router: Router
  ) {
    this.user = new User('abcdefghijkl', 'xy', 'xy@caffstore.hu');
  }

  ngOnInit(): void {
    this.loginForm = this._formBuilder.group({
      email: ['', [
        Validators.required,
        Validators.pattern(this.emailRegex)
      ]],
      password: ['', Validators.required]
    });
  }

  async login() {
      this._network.login(this.user.email, this.user.password).then(data => {
      this.user.id = data["id"];
      this.user.personName = data["username"];
      localStorage.setItem('token', data['token'].split(' ')[1]);
      console.log(this.user);
      localStorage.setItem('user_id', data['id']);
      this._router.navigate(['/' + RouterPath.main]);
    });
  }

}
