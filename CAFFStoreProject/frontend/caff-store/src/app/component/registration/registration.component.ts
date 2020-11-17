import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidationErrors, FormControl } from '@angular/forms';
import { User } from 'src/app/model/user';
import { NetworkService } from 'src/app/service/network/network.service';

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
    private _network: NetworkService
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
    this.newUser = new User('abcdefghijkl', 'xy', 'xy@caffstore.hu');
  }

  register() {
    this._network.register(this.newUser).then(data => {
      console.log("response: ", data);
    });
  }

}
