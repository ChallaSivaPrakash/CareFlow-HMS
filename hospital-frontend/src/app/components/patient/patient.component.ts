import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { DoctorService } from '../../services/doctor.service';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-patient',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './patient.component.html',
  styleUrls: ['./patient.component.css']
})
export class PatientComponent implements OnInit {

  patients: any[] = [];
  doctors: any[] = [];
  filteredDoctors: any[] = [];
  doctorSearchTerm: string = '';
  isEmergencyActive: boolean = false;
  
  patient: any = { 
  name: '', 
  age: null, 
  gender: '',
  chiefComplaint: '', 
  assignedDoctorId: null, 
  mobile: '',
  bloodPressure: '',
  heartRate: null,
  spO2: null,
  weight: null,            
  triageColor: 'GREEN'    
};
  
  selectedId: number = 0;
  
  // UI State Variable
  showForm: boolean = false;

constructor(
  private service: PatientService, 
  private doctorService: DoctorService, 
  private webSocketService: WebSocketService,
  private router: Router
) {}

  ngOnInit(): void {
    this.getPatients();
    this.getDoctors();
  }

  getDoctors() {
    this.doctorService.getAllDoctors().subscribe({
      next: (data: any) => {
        this.doctors = data;
        this.filteredDoctors = data;
      }
    });
  }

  filterDoctors() {
    this.filteredDoctors = this.doctors.filter(d => 
      d.name.toLowerCase().includes(this.doctorSearchTerm.toLowerCase()) || 
      d.department.toLowerCase().includes(this.doctorSearchTerm.toLowerCase())
    );
  }

  selectDoctor(dr: any) {
    this.patient.assignedDoctorId = dr.id;
    this.doctorSearchTerm = dr.name + ' (' + dr.department + ')';
    this.filteredDoctors = []; // Close dropdown
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) this.clearForm();
  }

  getPatients() {
    this.service.getPatients().subscribe({
      next: (data: any) => this.patients = data,
      error: (err) => console.log(err)
    });
  }

  addPatient() {
    // Frontend Validation Guard
    if (!this.patient.name || !this.patient.age || !this.patient.gender || !this.patient.chiefComplaint || !this.patient.assignedDoctorId) {
      window.alert('Please fill all required fields');
      return;
    }

    const payload = {
      ...this.patient,
      assignedDoctor: { id: this.patient.assignedDoctorId },
      status: 'WAITING'
    };

    this.service.addPatient(payload).subscribe({
      next: () => {
        this.getPatients();
        this.toggleForm(); // Hide form on success
      }
    });
  }

  editPatient(p: any) {
    this.patient = { 
      ...p, 
      assignedDoctorId: p.assignedDoctor?.id || null,
      chiefComplaint: p.chiefComplaint || p.disease // Mapping legacy if any
    };
    this.doctorSearchTerm = p.assignedDoctor ? p.assignedDoctor.name : '';
    this.selectedId = p.id;
    this.showForm = true; // Open form when editing
  }

  updatePatient() {
    // Frontend Validation Guard
    if (!this.patient.name || !this.patient.age || !this.patient.gender || !this.patient.chiefComplaint || !this.patient.assignedDoctorId) {
      window.alert('Please fill all required fields');
      return;
    }

    const payload = {
      ...this.patient,
      assignedDoctor: { id: this.patient.assignedDoctorId }
    };

    this.service.updatePatient(this.selectedId, payload).subscribe({
      next: () => {
        this.getPatients();
        this.toggleForm();
      }
    });
  }

  deletePatient(id: number) {
    if(confirm("Are you sure you want to remove this patient record?")) {
      this.service.deletePatient(id).subscribe({
        next: () => this.getPatients()
      });
    }
  }

  logout() {
    // Remove the token from the browser's memory
    localStorage.removeItem('jwt_token');
    // Send them back to the login screen
    this.router.navigate(['/login']);
  }

  toggleEmergency() {
    this.isEmergencyActive = !this.isEmergencyActive;
    this.webSocketService.triggerEmergencyOverride({ active: this.isEmergencyActive });
  }

  clearForm() {
    this.patient = { 
      name: '', 
      age: null, 
      gender: '',
      chiefComplaint: '', 
      assignedDoctorId: null, 
      mobile: '',
      bloodPressure: '',
      heartRate: null,
      spO2: null
    };
    this.doctorSearchTerm = '';
    this.selectedId = 0;
  }
}