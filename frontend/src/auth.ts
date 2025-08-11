import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8094/library/test';
  private loggedIn = new BehaviorSubject<boolean>(false);

  constructor( private http: HttpClient, @Inject(PLATFORM_ID) private platformId: Object ) {}

  login(credentials: { username: string; password: string }): Observable<any> {
    return this.http.post(`${this.API_URL}/login`, credentials, { withCredentials: true }).pipe(
        
      tap(() => {
        this.loggedIn.next(true);
      })
    );
  }

  verifyCode(code: string): Observable<any> {
    return this.http.post(`${this.API_URL}/securecode`, { code }, { withCredentials: true }).pipe(
      tap(() => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem('authenticated', 'true');
        }
        this.loggedIn.next(true);
      })
    );
  }

  isAuthenticated(): boolean {
    return isPlatformBrowser(this.platformId) && localStorage.getItem('authenticated') === 'true';
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem('authenticated');
      this.loggedIn.next(false);
      window.location.href = `${this.API_URL}/logout`;
    }
  }

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }
}