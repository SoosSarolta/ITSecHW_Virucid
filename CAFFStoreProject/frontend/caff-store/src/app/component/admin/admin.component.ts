import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {NetworkService} from '../../service/network/network.service';
import {Router} from '@angular/router';
import {AuthService} from '../../service/auth/auth.service';
import {User} from '../../model/user';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import { RouterPath } from 'src/app/util/router-path';
import {Caff} from '../../model/caff';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss']
})
export class AdminComponent implements OnInit, AfterViewInit {

  users: Array<User>;
  displayedColumns: string[] = ['personName', 'email', 'id', 'modify', 'delete'];
  dataSource: MatTableDataSource<User>;
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;

  caffs: Array<Caff>;

  constructor(
    private _network: NetworkService,
    private _router: Router,
    private _auth: AuthService
  ) { }

  ngOnInit(): void {
    // TODO: load from backend
    this.users = new Array();
    // let NagyLajosUser = new User();
    // NagyLajosUser.id = '000001';
    // NagyLajosUser.personName = 'Nagy Lajos';
    // NagyLajosUser.email = 'lajos@caffstore.hu';
    // this.users.push(NagyLajosUser);
    /* this.users.push(new User('000002', 'Nagyne Iren', 'iren@caffstore.hu'));
    this.users.push(new User('000003', 'Hurutos Sandor', 'sandor@caffstore.hu'));
    this.users.push(new User('000004', 'Vegh Bela', 'bela@caffstore.hu')); */
    this.loadUsers();
  }

  private async loadUsers() {
    await this._network.admin().then(data => {
      console.log(data);
      data.forEach(element => {
        const user = new User();
        user.id = element.id;
        user.personName = element.username;
        user.email = 'Test email';
        this.users.push(user);
        console.log(this.users);
      });
      this.dataSource = new MatTableDataSource(this.users);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }).catch(err => {
      console.log(err);
    });
    console.log(this.users);
    this.caffs = new Array();
    for (let i = 0; i < 10; i++) {
      this.caffs.push(new Caff('1', 'abc', ''));
    }
  }

  ngAfterViewInit(): void {
  }

  deleteUser(id: string): void {
    // TODO: call deleteUser(id) in backend
    if (confirm('Are you sure to delete this profile with id "' + id + '"?')) {
      console.log('Implement delete functionality here');
      this._network.deleteUser(id).then(data => {
        console.log(data);
      }).catch(err => {
        console.log(err);
      });
      this._router.navigate(['/' + RouterPath.admin]);
    }
  }

  modifyUser(id: string): void {
    this._router.navigate(['/' + RouterPath.profil], { queryParams: { id } });
  }

  navigateToProfile(): void {
    this._router.navigate(['/' + RouterPath.profil], { queryParams: { id: localStorage.getItem('user_id') } });
  }

  logout(): void {
    this._auth.logout();
    this._router.navigate(['/' + RouterPath.login]);
  }

}
