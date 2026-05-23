import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) { }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
      tap((response: any) => {
        if (response && response.token) {
          // Save the token
          localStorage.setItem('jwt_token', response.token);
          // Save the role directly from the backend response!
          if (response.role) {
            localStorage.setItem('user_role', response.role);
          }
        }
      })
    );
  }

  logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user_role');
  }

  getToken(): string | null {
    return localStorage.getItem('jwt_token');
  }

  getUserRole(): string | null {
    // Read the role straight from memory instead of decoding the token
    return localStorage.getItem('user_role');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getUserByUsername(username: string): Observable<any> {
    return this.http.get(`http://localhost:8080/api/users/${username}`);
  }

  updateUser(id: number, userData: any): Observable<any> {
    return this.http.put(`http://localhost:8080/api/users/${id}`, userData);
  }
}