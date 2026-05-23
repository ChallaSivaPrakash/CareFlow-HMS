import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { PatientService } from '../../services/patient.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-patient-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './patient-dashboard.html',
  styleUrl: './patient-dashboard.css',
})
export class PatientDashboardComponent implements OnInit {
  medicalRecord: any = null;
  isLoading: boolean = true;
  errorMessage: string = '';
  today: Date = new Date();

  constructor(
    private patientService: PatientService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.fetchMedicalRecord();
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
