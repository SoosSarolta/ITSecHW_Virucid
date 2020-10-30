import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidationErrors, FormControl } from '@angular/forms';

@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.scss']
})
export class RegistrationComponent implements OnInit {
  hidePassword = true;
  hideRepeatPassword = true;
  showDetails: boolean = true;
  name: string;
  email: string;
  password: string;
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
    private _formBuilder: FormBuilder
  ) { }

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
  }

  onStrengthChanged(strength: number) {
    console.log('password strength = ', strength);
  }

  register() {
    console.log(this.name, this.email);
  }

}
