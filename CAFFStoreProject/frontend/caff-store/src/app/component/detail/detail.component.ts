import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Caff } from 'src/app/model/caff';
import { NetworkService } from 'src/app/service/network/network.service';

@Component({
  selector: 'app-detail',
  templateUrl: './detail.component.html',
  styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
  currentCaff: Caff;

  constructor(
    private _router: ActivatedRoute,
    private _network: NetworkService
  ) {
  }

  ngOnInit(): void {
    this._router.queryParams.subscribe(params => {
      console.log(params['id']);
      this._network.details(params['id']).then(data => {
        console.log(data);
      }).catch(err => {
        console.log(err);
      });
    });
  }


  downloadCaff() {
    console.log("downloading caff...");
  }
}
