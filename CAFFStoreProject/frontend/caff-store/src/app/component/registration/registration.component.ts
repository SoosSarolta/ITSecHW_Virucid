import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidationErrors, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/model/user';
import { NetworkService } from 'src/app/service/network/network.service';
import { RouterPath } from 'src/app/util/router-path';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {
  newUser: User;
  hidePassword = true;
  hideRepeatPassword = true;
  showDetails: boolean = true;
  nameFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(2)
  ]);
  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  passwordFormControl = new FormControl('', [
    Validators.required,
    Validators.minLength(8),
    Validators.maxLength(30)
  ]);
  registerForm: FormGroup;
  emailRegex = '^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,20}$';

  constructor(
    private _formBuilder: FormBuilder,
    private _network: NetworkService,
    private _router: Router
  ) {
  }

  ngOnInit(): void {
    this.registerForm = this._formBuilder.group({
      personName: ['', [
        Validators.required,
        Validators.pattern('^[a-zA-ZáéíóöőúűÁÉÍÓÖŐÚŰ .-]*$'),
        Validators.minLength(2),
      ]],
      email: ['', [
        Validators.required,
        Validators.pattern(this.emailRegex)
      ]],
      password: ['', Validators.required]
    });
    this.newUser = new User();
  }

  register(): void {
    this._network.register(this.newUser).then(data => {
      this._router.navigate([RouterPath.login]);
    }).catch(err => {
      alert('This email is alredy registered!');
      window.location.reload();
    });
  }

}
