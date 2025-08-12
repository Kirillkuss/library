import { Component, OnInit,signal  } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AuthService } from '../auth';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './app.html'
})
export class App implements OnInit {

  protected readonly title = signal('frontend');

  constructor(public authService: AuthService) {}

  ngOnInit() {
    console.log(" App  ngOnInit");
    this.authService.checkAuth().subscribe();
  }

  logout() {
    console.log(" App  logout");
    this.authService.logout().subscribe();
  }
}