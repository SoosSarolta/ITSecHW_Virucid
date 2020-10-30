import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from "@angular/material/icon";
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RegistrationComponent } from './component/registration/registration.component';
import { LoginComponent } from './component/login/login.component';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';

@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatPasswordStrengthModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
