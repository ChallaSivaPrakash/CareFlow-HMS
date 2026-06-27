import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PatientService } from '../../services/patient.service';
import { AuthService } from '../../services/auth.service';
import { AppointmentService } from '../../services/appointment.service';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './patient-dashboard.html',
  styleUrl: './patient-dashboard.css',
})
export class PatientDashboardComponent implements OnInit {
  medicalRecord: any = null;
  appointments: any[] = [];
  isLoading: boolean = true;
  errorMessage: string = '';
  today: Date = new Date();

  // AI Booking form
  bookingForm = {
    age: null,
    chiefComplaint: '',
    contact: '',
    email: ''
  };
  isBooking: boolean = false;

  constructor(
    private patientService: PatientService,
    private authService: AuthService,
    private appointmentService: AppointmentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchMedicalRecord();
    this.fetchAppointments();
  }

  fetchMedicalRecord(): void {
    this.isLoading = true;
    this.patientService.getMyMedicalRecord().subscribe({
      next: (data) => {
        this.medicalRecord = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching medical record', err);
        this.errorMessage = 'Failed to load your health portal. Please try again later.';
        this.isLoading = false;
      }
    });
  }

  fetchAppointments(): void {
    this.appointmentService.getMyAppointments().subscribe({
      next: (data) => {
        this.appointments = data;
      },
      error: (err) => {
        console.error('Error fetching appointments', err);
      }
    });
  }

  submitBooking(): void {
    this.isBooking = true;
    // TODO: Call AI booking API
    setTimeout(() => {
      this.isBooking = false;
      alert('Booking request submitted! We will contact you shortly.');
    }, 1500);
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
      case 'ADMITTED': return 'bg-primary';
      case 'WAITING': return 'bg-info text-dark';
      case 'DISCHARGED': return 'bg-success';
      case 'EMERGENCY': return 'bg-danger';
      default: return 'bg-secondary';
    }
  }
}
