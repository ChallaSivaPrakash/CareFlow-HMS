import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router'; // <-- ADD THIS LINE
import { PatientService } from '../../services/patient.service';
import { AuthService } from '../../services/auth.service';
import { WebSocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-doctor-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './doctor-dashboard.html',
  styleUrl: './doctor-dashboard.css',
})
export class DoctorDashboardComponent implements OnInit {
  patients: any[] = [];
  doctorName: string = 'Doctor';
  isLoading: boolean = true;
  isSubmitting: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  today: Date = new Date();

  // Edit Modal State
  showEditModal = false;
  editingPatient: any = null;

  constructor(
    private patientService: PatientService,
    private authService: AuthService,
    private webSocketService: WebSocketService,
    private cdr: ChangeDetectorRef,
    private router: Router // <-- ADD THIS LINE
  ) {}

  ngOnInit(): void {
    this.loadDoctorInfo();
    this.fetchPatients();
  }

  loadDoctorInfo(): void {
    // We can try to extract name from JWT if available, or just use a placeholder
    // Our AuthService already has a getUserRole, maybe we can add a getName
    const token = this.authService.getToken();
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        this.doctorName = payload.sub || 'Doctor'; // sub is usually the username
      } catch (e) {
        console.error('Error parsing token for name', e);
      }
    }
  }

  fetchPatients(): void {
    this.isLoading = true;
    this.patientService.getMyPatients().subscribe({
      next: (data) => {
        // Filter out DISCHARGED patients
        this.patients = data.filter(p => p.status?.toUpperCase() !== 'DISCHARGED');
        this.isLoading = false;
        this.cdr.detectChanges(); // <--- ENSURE THIS IS HERE
      },
      error: (err) => {
        console.error('Error fetching patients', err);
        this.errorMessage = 'Failed to load patient records. Please try again later.';
        this.isLoading = false;
        this.cdr.detectChanges(); // <--- AND HERE
      }
    });
  }

  dischargePatient(patient: any): void {
    if (confirm(`Are you sure you want to discharge ${patient.name}?`)) {
      const updatedPatient = { ...patient, status: 'DISCHARGED' };
      this.patientService.updatePatient(patient.id, updatedPatient).subscribe({
        next: () => {
          this.successMessage = 'Patient discharged successfully.';
          this.fetchPatients();
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error discharging patient', err);
          this.errorMessage = 'Failed to discharge patient.';
          this.cdr.detectChanges();
        }
      });
    }
  }

  viewDetails(patient: any): void {
    this.patientService.getPatientById(patient.id).subscribe({
      next: (fullPatient: any) => {
        console.log('Viewing details for patient:', fullPatient);
        alert(`Details for ${fullPatient.name}:\nID: ${fullPatient.patientId}\nDoctor: ${fullPatient.assignedDoctor?.name || 'N/A'}\nComplaint: ${fullPatient.chiefComplaint}\nStatus: ${fullPatient.status}`);
      },
      error: (err) => console.error('Error fetching patient details', err)
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  getTriageBadgeClass(color: string): string {
    switch (color?.toUpperCase()) {
      case 'RED': return 'bg-danger';
      case 'YELLOW': return 'bg-warning text-dark';
      case 'GREEN': return 'bg-success';
      default: return 'bg-secondary';
    }
  }

  getStatusBadgeClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'ADMITTED': return 'bg-success';
      case 'WAITING': return 'bg-warning text-dark';
      case 'DISCHARGED': return 'bg-secondary';
      case 'EMERGENCY': return 'bg-danger';
      default: return 'bg-info';
    }
  }

  // Modal Methods
  openEditModal(patient: any): void {
    this.patientService.getPatientById(patient.id).subscribe({
      next: (fullPatient) => {
        this.editingPatient = { ...fullPatient };
        this.showEditModal = true;
      },
      error: (err) => console.error('Error fetching patient details', err)
    });
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.editingPatient = null;
  }

  onUpdatePatient(): void {
    if (!this.editingPatient) return;

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.patientService.updatePatient(this.editingPatient.id, this.editingPatient).subscribe({
      next: () => {
        this.successMessage = 'Patient record updated successfully!';
        this.closeEditModal();
        this.fetchPatients();
        this.isSubmitting = false;
      },
      error: (err) => {
        console.error('Error updating patient', err);
        this.errorMessage = 'Failed to update patient details.';
        this.isSubmitting = false;
      }
    });
  }
}
