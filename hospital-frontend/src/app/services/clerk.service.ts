import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OpdClerkService {
  private api = "http://localhost:8080/api/clerks";

  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = localStorage.getItem('jwt_token') || '';
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  getAllClerks(): Observable<any[]> {
    return this.http.get<any[]>(this.api, this.getAuthHeaders());
  }

  addClerk(clerkData: any): Observable<any> {
    return this.http.post<any>(this.api, clerkData, this.getAuthHeaders());
  }

  updateClerk(id: number, clerkData: any): Observable<any> {
    return this.http.put<any>(`${this.api}/${id}`, clerkData, this.getAuthHeaders());
  }

  deleteClerk(id: number): Observable<any> {
    return this.http.delete<any>(`${this.api}/${id}`, this.getAuthHeaders());
  }
}
