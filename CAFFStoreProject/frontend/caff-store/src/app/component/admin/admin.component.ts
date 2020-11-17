import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {NetworkService} from '../../service/network/network.service';
import {Router} from '@angular/router';
import {AuthService} from '../../service/auth/auth.service';
import {User} from '../../model/user';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';

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
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(
    private _network: NetworkService,
    private _router: Router,
    private _auth: AuthService
  ) { }

  ngOnInit(): void {
    // TODO: load from backend
    this.users = new Array();
    this.users.push(new User('000001', 'Nagy Lajos', 'lajos@caffstore.hu'));
    this.users.push(new User('000002', 'Nagyne Iren', 'iren@caffstore.hu'));
    this.users.push(new User('000003', 'Hurutos Sandor', 'sandor@caffstore.hu'));
    this.users.push(new User('000004', 'Vegh Bela', 'bela@caffstore.hu'));

    this.dataSource = new MatTableDataSource(this.users);
  }

  ngAfterViewInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
  }

  deleteUser(id: string): void {
    // TODO: call deleteUser() in backend
    if (confirm('Are you sure to delete this profile with id "' + id + '"?')) {
      console.log('Implement delete functionality here');
    }
  }

  modifyUser(id: string): void {
    // TODO: navigate to user profile
    // this._router.navigate(['/' + RouterPath.profil], { queryParams: { id: id } });

  }

  // TODO : add id argument after valid login
  navigateToProfile(): void{
    // this._router.navigate(['/' + RouterPath.profil], { queryParams: { id: id } });
  }

  logout(): void {
    this._auth.logout();
    this._router.navigate(['login']);
  }

}
