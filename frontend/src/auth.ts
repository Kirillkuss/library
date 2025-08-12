import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8094/library';
  private isAuth = false;

  constructor(private http: HttpClient) {}

  isAuthenticated(): boolean {
    console.log( "4. AuthService isAuthenticated");
    return this.isAuth;
  }

  redirectToLogin(): void {
    console.log( "5. AuthService redirectToLogin");
    window.location.href = `${this.apiUrl}/login?redirect=${encodeURIComponent(window.location.href)}`;
  }

  checkAuth(): Observable<boolean> {
    console.log( "1. AuthService checkAuth");
    return this.http.get(`${this.apiUrl}/app/index.html`, { 
      withCredentials: true,
      observe: 'response'
    }).pipe(
      map(response => {
        this.isAuth = response.ok;
        return response.ok;
      }),
      catchError(() => {
        this.isAuth = false;
        return of(false);
      })
    );
  }

  logout(): Observable<any> {
    console.log( "2. AuthService logout");
    return this.http.post(`${this.apiUrl}/logout`, {}, {
      withCredentials: true,
      responseType: 'text'
    }).pipe(
      tap(() => {
        this.isAuth = false;
        this.redirectToLogin();
      })
    );
  }

  // Пример запроса к вашему API
  getUsersCount(): Observable<any> {
    console.log( "3. AuthService getUsersCount");
    return this.http.get(`${this.apiUrl}/users/counts`, {
      withCredentials: true
    });
  }
}