import { Injectable } from '@angular/core'; 
import { HttpClient, HttpHeaders } from '@angular/common/http'; 
import { Observable, timer } from 'rxjs'; 
import { retry } from 'rxjs/operators'; 

@Injectable({ 
  providedIn: 'root' 
}) 
export class AppointmentService { 
  private apiUrl = 'http://localhost:8080/api'; 

  constructor(private http: HttpClient) { } 

  private getAuthHeaders() {
    const token = localStorage.getItem('jwt_token');
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token || ''}`
      })
    };
  }

  getDoctors(): Observable<any[]> { 
    return this.http.get<any[]>(`${this.apiUrl}/doctors`); 
  } 

  getAvailableSlots(doctorId: number, date: string): Observable<string[]> { 
    return this.http.get<string[]>(`${this.apiUrl}/appointments/doctor/${doctorId}/slots?date=${date}`, this.getAuthHeaders()); 
  } 

  bookAppointment(request: any): Observable<any> { 
    return this.http.post<any>(`${this.apiUrl}/appointments/book`, request, this.getAuthHeaders()).pipe( 
      retry({ 
        count: 3, 
        delay: (error, retryCount) => { 
          if (error.status === 409) { 
            return timer(1000 * retryCount); 
          } 
          throw error; 
        } 
      }) 
    ); 
  } 

  getMyAppointments(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/appointments/me`, this.getAuthHeaders());
  }
} 
