// import { NgModule } from '@angular/core';
// import { Routes, RouterModule } from '@angular/router';
// import { RegistrationComponent } from '../component/registration/registration.component';
// import { LoginComponent } from '../component/login/login.component';
// import { DetailComponent } from '../component/detail/detail.component';
// import { MainComponent } from '../component/main/main.component';
// import { AdminComponent } from '../component/admin/admin.component';
// import { ProfileComponent } from '../component/profile/profile.component';
// import { AuthGuard } from './auth.guard';
//
//
// const routes: Routes = [
//   { path: 'registration', component: RegistrationComponent },
//   { path: 'login', component: LoginComponent },
//   { path: 'detail', component: DetailComponent, canActivate: [AuthGuard] },
//   { path: 'main', component: MainComponent, canActivate: [AuthGuard] },
//   { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
//   { path: 'profil', component: ProfileComponent, canActivate: [AuthGuard] },
//   { path: '', redirectTo: 'login', pathMatch: 'full' },
//   { path: '**', redirectTo: 'login', pathMatch: 'full' }];
//
// @NgModule({
//   imports: [RouterModule.forRoot(routes)],
//   exports: [RouterModule]
// })
// export class RouterPath { }


export class RouterPath {
  public static readonly main: string = 'main';
  public static readonly login: string = 'login';
  public static readonly registration: string = 'registration';
  public static readonly detail: string = 'detail';
  public static readonly admin: string = 'admin';
  public static readonly profil: string = 'profil';

  public static readonly guestRoutes: Array<string> = new Array<string>(
    RouterPath.login,
    RouterPath.registration
  );

  public static readonly userRoutes: Array<string> = new Array<string>(
    RouterPath.main,
    RouterPath.detail,
    RouterPath.profil
  );

  public static readonly adminRoutes: Array<string> = new Array<string>(
    RouterPath.login,
    RouterPath.registration,
    RouterPath.main,
    RouterPath.detail,
    RouterPath.admin,
    RouterPath.profil
  );
}
