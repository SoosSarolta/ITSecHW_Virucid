import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidationErrors, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/model/user';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';
import {AuthService} from '../../service/auth/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  hidePassword = true;
  showDetails = true;
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
    private _router: Router,
    private _auth: AuthService
  ) {
    this.user = new User();
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

  async login(): Promise<any> {
    this._network.login(this.user.email, this.user.password).then(data => {
      this.user.id = data.id;
      this.user.personName = data.username;
      this.user.role = data.role;

      this._auth.login(data.id, data.token, data.role);
      this._router.navigate([RouterPath.main]);
    });
  }
}
