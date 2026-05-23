import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class DoctorService {
  private api = "http://localhost:8080/api/doctors";

  constructor(private http: HttpClient) {}

  private getAuthHeaders() {
    const token = localStorage.getItem('jwt_token') || '';
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  getAllDoctors(): Observable<any[]> {
    return this.http.get<any[]>(this.api, this.getAuthHeaders());
  }

  addDoctor(doctorData: any): Observable<any> {
    return this.http.post<any>(this.api, doctorData, this.getAuthHeaders());
  }

  updateDoctor(id: number, doctorData: any): Observable<any> {
    return this.http.put<any>(`${this.api}/${id}`, doctorData, this.getAuthHeaders());
  }

  deleteDoctor(id: number): Observable<any> {
    return this.http.delete<any>(`${this.api}/${id}`, this.getAuthHeaders());
  }
}
