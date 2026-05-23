import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  api = "http://localhost:8080/api/patients";

  constructor(private http: HttpClient) {}

  // Helper method to attach your JWT badge to every request
  private getAuthHeaders() {
    const token = localStorage.getItem('jwt_token') || '';
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token}`
      })
    };
  }

  // GET
  getPatients() {
    return this.http.get(this.api, this.getAuthHeaders());
  }

  // GET My Patients (Specific for Doctor Dashboard)
  getMyPatients() {
    return this.http.get<any[]>(this.api, this.getAuthHeaders());
  }

  // GET My Medical Record (Specific for Patient Dashboard)
  getMyMedicalRecord() {
    return this.http.get<any>(`${this.api}/me`, this.getAuthHeaders());
  }

  // SEARCH Patients
  searchPatients(name?: string, patientId?: string): Observable<any[]> {
    let params = '';
    if (name) params += `name=${name}&`;
    if (patientId) params += `patientId=${patientId}`;
    return this.http.get<any[]>(`${this.api}?${params}`, this.getAuthHeaders());
  }

  // ADD
  addPatient(patient: any) {
    return this.http.post(this.api, patient, this.getAuthHeaders());
  }

  // UPDATE
  updatePatient(id: number, patient: any) {
    return this.http.put(`${this.api}/${id}`, patient, this.getAuthHeaders());
  }

  // DELETE
  // Note: Added responseType: 'text' because your Spring Boot delete endpoint returns a plain string
  deletePatient(id: number) {
    return this.http.delete(`${this.api}/${id}`, { 
      headers: this.getAuthHeaders().headers,
      responseType: 'text' 
    });
  }
}