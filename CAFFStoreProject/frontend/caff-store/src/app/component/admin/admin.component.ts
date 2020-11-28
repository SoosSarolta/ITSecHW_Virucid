import {AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {NetworkService} from '../../service/network/network.service';
import {Router} from '@angular/router';
import {AuthService} from '../../service/auth/auth.service';
import {User} from '../../model/user';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import { RouterPath } from 'src/app/util/router-path';
import {Caff} from '../../model/caff';
import {DomSanitizer} from '@angular/platform-browser';
import {MatTabChangeEvent} from '@angular/material/tabs';

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
    private _auth: AuthService,
    private _sanitization: DomSanitizer,
    private _changeDetectorRefs: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    // TODO: load from backend
    this.users = [];
    this.caffs = [];
    this.loadUsers();
    this.loadCaffs();
  }

  onTabChanged(event: MatTabChangeEvent): void {
    if (event.index === 0) {
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }
  }

  private async loadUsers(): Promise<any> {
    await this._network.getUsers().then(data => {
      console.log(data);
      data.forEach(element => {
        const user = new User();
        user.id = element.id;
        user.personName = element.username;
        user.email = element.email;
        user.role = element.role;
        this.users.push(user);
        console.log(this.users);
      });
      this.dataSource = new MatTableDataSource(this.users);
      this.dataSource.sort = this.sort;
      this.dataSource.paginator = this.paginator;
    }).catch(err => {
      console.log(err);
      switch (err.status) {
        case 400:
          this._router.navigate(['error', 'bad-request']);
          break;
        case 403:
          this._router.navigate(['error', 'forbidden']);
          break;
        case 404:
          this._router.navigate(['error', 'not-found']);
          break;
        default:
          this._router.navigate(['login']);
          break;
      }
    });
    console.log(this.users);
  }

  private async loadCaffs(): Promise<any> {
    await this._network.getCaffs().then(data => {
      console.log(data);
      data.forEach(element => {
        const url = 'data:image/JPEG;base64,' + encodeURIComponent(element.bitmapFile);
        const image = this._sanitization.bypassSecurityTrustResourceUrl(url);
        this.caffs.push(new Caff(element.id, element.originalFileName, image));
        console.log('this.caffs: ', this.caffs);
      });
    }).catch(err => {
      console.log(err);
    });
  }

  ngAfterViewInit(): void {
    this._changeDetectorRefs.detectChanges();
  }

  deleteUser(id: string): void {
    if (confirm('Are you sure to delete this profile with id "' + id + '"?')) {
      this._network.deleteUser(id).then(data => {
        console.log(data);
      }).catch(err => {
        console.log(err);
      });
      window.location.reload();
    }
  }

  modifyUser(id: string): void {
    this._router.navigate(['profil'], { queryParams: { id } });
  }

  deleteCaff(id: string): void {
    // TODO: call deleteUser(id) in backend
    if (confirm('Are you sure to delete this caff file with id "' + id + '"?')) {
      this._network.deleteCaff(id).then(data => {
        console.log(data);
      }).catch(err => {
        console.log(err);
      });
      window.location.reload();
    }
  }
}
