import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import {MatCardModule} from '@angular/material/card';
import { NgxFileDropModule } from 'ngx-file-drop';
import { FileSaverModule } from 'ngx-filesaver';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RegistrationComponent } from './component/registration/registration.component';
import { LoginComponent } from './component/login/login.component';
import { MatPasswordStrengthModule } from '@angular-material-extensions/password-strength';
import { MainComponent } from './component/main/main.component';
import { AdminComponent } from './component/admin/admin.component';
import { DetailComponent } from './component/detail/detail.component';
import { ProfileComponent } from './component/profile/profile.component';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule} from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatDialogModule } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { MatTabsModule } from '@angular/material/tabs';
import { NotFoundComponent } from './component/error/not-found/not-found.component';
import { BadRequestComponent } from './component/error/bad-request/bad-request.component';
import { ForbiddenComponent } from './component/error/forbidden/forbidden.component';


@NgModule({
  declarations: [
    AppComponent,
    RegistrationComponent,
    LoginComponent,
    MainComponent,
    AdminComponent,
    DetailComponent,
    ProfileComponent,
    BadRequestComponent,
    ForbiddenComponent,
    NotFoundComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCardModule,
    FormsModule,
    MatListModule,
    MatDialogModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MatPasswordStrengthModule,
    NgxFileDropModule,
    FileSaverModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatTabsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
