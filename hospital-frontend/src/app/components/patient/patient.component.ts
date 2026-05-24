import { HttpClient } from '@angular/common/http';
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
  activePatients: any[] = [];
  dischargedPatients: any[] = [];
  doctors: any[] = [];
  filteredDoctors: any[] = [];
  doctorSearchTerm: string = '';
  
  patient: any = { 
    name: '', age: null, gender: '', chiefComplaint: '', 
    assignedDoctorId: null, mobile: '', bloodPressure: '', 
    heartRate: null, spO2: null, weight: null, triageColor: 'GREEN',
    visitType: 'CONSULTATION'    
  };
  
  selectedId: number = 0;
  showForm: boolean = false;
  showBedModal: boolean = false;
  suggestedBed: any = null;
  availableBeds: any[] = [];
  manualBedSelection: boolean = false;

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
    this.doctorService.getAllDoctors().subscribe(data => {
      this.doctors = data;
      this.filteredDoctors = data;
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
    this.filteredDoctors = []; 
  }

  toggleForm() {
    this.showForm = !this.showForm;
    if (!this.showForm) this.clearForm();
  }

  getPatients() {
    this.service.getPatients().subscribe((data) => {
      this.patients = data as any[];
      this.activePatients = this.patients.filter(p => p.status !== 'DISCHARGED');
      this.dischargedPatients = this.patients.filter(p => p.status === 'DISCHARGED');
    });
  }

  addPatient() {
    console.log('Debugging addPatient payload:', this.patient);
    if (!this.patient.name || !this.patient.age || !this.patient.gender || !this.patient.chiefComplaint || !this.patient.assignedDoctorId) {
      window.alert('Please fill all required fields (Name, Age, Gender, Complaint, Doctor)');
      return;
    }
    const payload = { 
      ...this.patient, 
      assignedDoctor: { id: this.patient.assignedDoctorId }, 
      status: 'WAITING' 
    };
    this.service.addPatient(payload).subscribe((res: any) => { 
      if (this.patient.visitType === 'ADMITTED') {
        this.service.suggestBed(res.triageColor).subscribe((bed: any) => {
          this.suggestedBed = bed;
          this.selectedId = res.id; // Store patient ID for allocation
          this.showBedModal = true;
          this.service.getAvailableBeds().subscribe((beds: any) => this.availableBeds = beds);
        });
      } else {
        this.getPatients(); 
        this.toggleForm();
      }
    });
  }

  confirmBedAllocation() {
    this.service.allocateBed(this.suggestedBed.id, this.selectedId.toString()).subscribe(() => {
      this.closeBedModal();
      this.getPatients();
      this.toggleForm();
    });
  }

  closeBedModal() {
    this.showBedModal = false;
    this.suggestedBed = null;
    this.manualBedSelection = false;
  }

  editPatient(p: any) {
    this.service.getPatientById(p.id).subscribe((fullPatient: any) => {
      this.patient = { ...fullPatient, assignedDoctorId: fullPatient.assignedDoctor?.id };
      this.selectedId = fullPatient.id;
      this.doctorSearchTerm = fullPatient.assignedDoctor ? 
        fullPatient.assignedDoctor.name + ' (' + fullPatient.assignedDoctor.department + ')' : '';
      this.showForm = true;
    });
  }

  updatePatient() {
    const payload = { 
      ...this.patient, 
      assignedDoctor: { id: this.patient.assignedDoctorId } 
    };
    this.service.updatePatient(this.selectedId, payload).subscribe(() => { 
      this.getPatients(); 
      this.toggleForm(); 
    });
  }

  // REPLACED deletePatient with dischargePatient
  dischargePatient(p: any) {
    if (confirm("Discharge " + p.name + "?")) {
      const updatedPatient = { ...p, status: 'DISCHARGED' };
      this.service.updatePatient(p.id, updatedPatient).subscribe(() => this.getPatients());
    }
  }

  logout() {
    localStorage.removeItem('jwt_token');
    this.router.navigate(['/login']);
  }

  clearForm() {
    this.patient = { name: '', age: null, gender: '', chiefComplaint: '', assignedDoctorId: null, mobile: '', bloodPressure: '', heartRate: null, spO2: null };
    this.doctorSearchTerm = '';
    this.selectedId = 0;
  }
}