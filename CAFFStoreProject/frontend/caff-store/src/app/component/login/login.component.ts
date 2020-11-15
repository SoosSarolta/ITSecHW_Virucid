import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidationErrors, FormControl } from '@angular/forms';
import { NetworkService } from 'src/app/service/network/network.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  hidePassword = true;
  showDetails: boolean = true;
  email: string;
  password: string;
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
    private _network: NetworkService
  ) { }

  ngOnInit(): void {
    this.loginForm = this._formBuilder.group({
      email: ['', [
        Validators.required,
        Validators.pattern(this.emailRegex)
      ]],
      password: ['', Validators.required]
    });
  }

  login() {
    console.log(this.email, this.password);
    this._network.login(this.email, this.password).then(data => {
      console.log("this._network.login: ", data);
    }).catch(err => {
      console.log(err);
    });
  }

}
