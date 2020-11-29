import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminComponent } from './admin.component';
import { RouterTestingModule } from '@angular/router/testing';
import { LoginComponent } from '../login/login.component';
import { BrowserModule, By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { NetworkService } from '../../service/network/network.service';
import {User} from '../../model/user';
import {Caff} from '../../model/caff';

describe('AdminComponent', () => {
  let component: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;

  const routerSpy = {navigate: jasmine.createSpy('navigate')};
  const networkSpy = {
    getUsers: jasmine.createSpy('getUsers'),
    getCaffs: jasmine.createSpy('getCaffs'),
    deleteUser: jasmine.createSpy('deleteUser'),
    modifyUser: jasmine.createSpy('modifyUser'),
    deleteCaff: jasmine.createSpy('deleteCaff')
  };


  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ RouterTestingModule, BrowserModule, HttpClientModule ],
      declarations: [ AdminComponent ],
      providers: [
        { provide: Router, useValue: routerSpy },
        { provide: NetworkService, useValue: networkSpy }
      ],
    })
    .compileComponents().then(() => {
        fixture = TestBed.createComponent(AdminComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
        spyOn(component, 'windowReload').and.callFake(() => {});
    });
  });

  it('should have been created', () => {
    expect(component).toBeTruthy();
  });

  it('ngOnInit should call networkService', async () => {
    networkSpy.getUsers.and.returnValue(Promise.resolve([]));
    networkSpy.getCaffs.and.returnValue(Promise.resolve([]));

    await component.ngOnInit();
    fixture.detectChanges();

    expect(networkSpy.getUsers).toHaveBeenCalled();
    expect(networkSpy.getCaffs).toHaveBeenCalled();
  });

  it('ngOnInit should load user(s)', async () => {
    networkSpy.getUsers.and.returnValue(Promise.resolve([{ id: 'U00000', username: 'ABC', email: 'abc@def.hu', role: 'USER' }]));
    networkSpy.getCaffs.and.returnValue(Promise.resolve([]));

    await component.ngOnInit();
    fixture.detectChanges();

    const u = new User();
    u.id = 'U00000';
    u.personName = 'ABC';
    u.email = 'abc@def.hu';
    u.role = 'USER';
    expect(component.users).toContain(u);
  });

  it('ngOnInit should load caff(s)', async () => {
    networkSpy.getUsers.and.returnValue(Promise.resolve([]));
    networkSpy.getCaffs.and.returnValue(Promise.resolve([{ id: 'C00000', bitmapFile: undefined, originalFileName: 'caff1.caff' }]));

    await component.ngOnInit();
    fixture.detectChanges();

    expect(component.caffs.length).toEqual(1);
  });

  it('deleteUser should call NetworkService', async () => {

    spyOn(window, 'confirm').and.returnValue(true);
    networkSpy.deleteUser.and.returnValue(Promise.resolve([]));
    component.deleteUser('ID0123');
    expect(networkSpy.deleteUser).toHaveBeenCalledWith('ID0123');
  });

  it('modifyUser should call Router', async () => {

    component.modifyUser('ID1122');
    expect(routerSpy.navigate).toHaveBeenCalledWith(['profil'], { queryParams: { id: 'ID1122' } });
  });

  it('deleteCaff should call NetworkService', async () => {

    spyOn(window, 'confirm').and.returnValue(true);
    networkSpy.deleteCaff.and.returnValue(Promise.resolve([]));
    component.deleteCaff('ID4567');
    expect(networkSpy.deleteCaff).toHaveBeenCalledWith('ID4567');
  });
});
