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
    const token = localStorage.getItem('jwt_token');
    if (!token) {
      console.error('PatientService: No JWT token found in localStorage!');
    }
    return {
      headers: new HttpHeaders({
        'Authorization': `Bearer ${token || ''}`
      })
    };
  }

  // GET
  getPatients() {
    return this.http.get(this.api);
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

  // GET by ID
  getPatientById(id: number) {
    return this.http.get(`${this.api}/${id}`, this.getAuthHeaders());
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

  // BEDS
  suggestBed(triageColor: string) {
    return this.http.get(`http://localhost:8080/api/beds/suggest?triageColor=${triageColor}`, this.getAuthHeaders());
  }

  getAvailableBeds() {
    return this.http.get(`http://localhost:8080/api/beds/available`, this.getAuthHeaders());
  }

  allocateBed(bedId: number, patientId: string) {
    return this.http.post(`http://localhost:8080/api/beds/allocate?bedId=${bedId}&patientId=${patientId}`, {}, this.getAuthHeaders());
  }
}