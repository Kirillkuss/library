import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
//import { AuthService } from '../auth';
import { isPlatformBrowser } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './app.html'
})
export class App{
    protected readonly title = signal('frontend');

   /**  constructor( private authService: AuthService, @Inject(PLATFORM_ID) private platformId: Object ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated() && isPlatformBrowser(this.platformId)) {
      window.location.href = 'http://localhost:8094/library/login';
    }
  }*/
}