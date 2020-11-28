import { DebugElement } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule, By } from '@angular/platform-browser';
import { Router } from '@angular/router';

import { RegistrationComponent } from './registration.component';
import { RouterPath } from 'src/app/util/router-path';
import { HttpClientModule } from '@angular/common/http';
import { User } from 'src/app/model/user';

describe('RegistrationComponent', () => {
  let component: RegistrationComponent;
  let fixture: ComponentFixture<RegistrationComponent>;
  let de: DebugElement;
  let el: HTMLElement;

  let routerSpy;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegistrationComponent ],
      providers: [
        { provide: Router, useValue: routerSpy }
      ],
      imports: [ BrowserModule,
                 FormsModule,
                 ReactiveFormsModule,
                 HttpClientModule
               ]
    })
    .compileComponents().then(() => {
      fixture = TestBed.createComponent(RegistrationComponent);
      component = fixture.componentInstance;

      de = fixture.debugElement.query(By.css('form'));
      el = de.nativeElement;

      routerSpy = {navigate: jasmine.createSpy('navigate')};
    });
  });

  it('should have hide password', async () => {
    expect(component.hidePassword).toBeTruthy();
  });

  it('should have hide repeat password', async () => {
    expect(component.hideRepeatPassword).toBeTruthy();
  });

  it('should have show details', async () => {
    expect(component.showDetails).toBeTruthy();
  });

  it('form should be invalid', async() => {
    component.nameFormControl.setValue('');
    expect(component.nameFormControl.valid).toBeFalsy();
    component.emailFormControl.setValue('');
    expect(component.emailFormControl.valid).toBeFalsy();
    component.passwordFormControl.setValue('');
    expect(component.passwordFormControl.valid).toBeFalsy();
  });

  it('form should be valid', async() => {
    component.nameFormControl.setValue('abc');
    expect(component.nameFormControl.valid).toBeTruthy();
    component.emailFormControl.setValue('abc@efd.hu');
    expect(component.emailFormControl.valid).toBeTruthy();
    component.passwordFormControl.setValue('Test1234!');
    expect(component.passwordFormControl.valid).toBeTruthy();
  });
});
