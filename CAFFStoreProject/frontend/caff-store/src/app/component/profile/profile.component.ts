import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NetworkService } from 'src/app/service/network/network.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {

  constructor(
    private _router: ActivatedRoute,
    private _network: NetworkService
  ) { }

  ngOnInit(): void {
    this._router.queryParams.subscribe(params => {
      console.log(params['id']);
      this._network.profile(params['id']).then(data => {
        console.log(data);
      }).catch(err => {
        console.log(err);
      });
    });
  }

}
