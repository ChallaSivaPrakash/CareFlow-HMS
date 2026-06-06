import { Injectable } from '@angular/core'; 
 import { HttpClient } from '@angular/common/http'; 
 import { Observable, timer } from 'rxjs'; 
 import { retry } from 'rxjs/operators'; 
 
 @Injectable({ 
   providedIn: 'root' 
 }) 
 export class AppointmentService { 
   private apiUrl = 'http://localhost:8080/api'; 
 
   constructor(private http: HttpClient) {} 
 
   getDoctors(): Observable<any[]> { 
     return this.http.get<any[]>(`${this.apiUrl}/doctors`); 
   } 
 
   getAvailableSlots(doctorId: number, date: string): Observable<string[]> { 
     return this.http.get<string[]>(`${this.apiUrl}/appointments/doctor/${doctorId}/slots?date=${date}`); 
   } 
 
   bookAppointment(request: any): Observable<any> { 
     return this.http.post<any>(`${this.apiUrl}/appointments/book`, request).pipe( 
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
 } 
