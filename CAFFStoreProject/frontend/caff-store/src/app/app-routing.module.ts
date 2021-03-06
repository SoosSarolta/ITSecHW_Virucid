import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RegistrationComponent } from './component/registration/registration.component';
import { LoginComponent } from './component/login/login.component';
import { DetailComponent } from './component/detail/detail.component';
import { MainComponent } from './component/main/main.component';
import { AdminComponent } from './component/admin/admin.component';
import { ProfileComponent } from './component/profile/profile.component';
import { AuthGuard } from './util/auth.guard';
import { NotFoundComponent } from './component/error/not-found/not-found.component';
import { ForbiddenComponent } from './component/error/forbidden/forbidden.component';
import { BadRequestComponent } from './component/error/bad-request/bad-request.component';


const routes: Routes = [
  { path: 'registration', component: RegistrationComponent },
  { path: 'login', component: LoginComponent },
  { path: 'detail', component: DetailComponent, canActivate: [AuthGuard] },
  { path: 'main', component: MainComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminComponent, canActivate: [AuthGuard] },
  { path: 'profil', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'error/bad-request', component: BadRequestComponent, canActivate: [AuthGuard] },
  { path: 'error/forbidden', component: ForbiddenComponent, canActivate: [AuthGuard] },
  { path: 'error/not-found', component: NotFoundComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login', pathMatch: 'full' }];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
