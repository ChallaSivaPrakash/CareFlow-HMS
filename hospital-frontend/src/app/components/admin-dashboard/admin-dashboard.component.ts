import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { DoctorService } from '../../services/doctor.service';
import { AuthService } from '../../services/auth.service';
import { OpdClerkService } from '../../services/clerk.service';
import { PatientService } from '../../services/patient.service';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})
export class AdminDashboardComponent implements OnInit {
  doctors: any[] = [];
  clerks: any[] = [];
  
  newDoctor = {
    name: '',
    specialty: '',
    department: '',
    contactNumber: '',
    email: '',
    address: '',
    bio: '',
    profileImageUrl: ''
  };

  newClerk = {
    name: '',
    email: '',
    contactNumber: '',
    department: '',
    profileImageUrl: ''
  };

  isLoading = true;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  // Staff View State
  activeTab: 'doctors' | 'clerks' = 'doctors';

  // Edit Modal State
  showEditModal = false;
  editingStaff: any = null;
  staffType: 'doctor' | 'clerk' = 'doctor';

  // Overview Modal State
  showOverviewModal = false;
  overviewDoctor: any = null;

  // Patient Search State
  searchName: string = '';
  searchPatientId: string = '';
  searchResults: any[] = [];
  showPatientHistoryModal = false;
  selectedPatient: any = null;

  // File Upload State
  fileName: string = '';

  constructor(
    private doctorService: DoctorService,
    private clerkService: OpdClerkService,
    private patientService: PatientService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStaff();
  }

  loadStaff(): void {
    this.isLoading = true;
    Promise.all([
      this.doctorService.getAllDoctors().toPromise(),
      this.clerkService.getAllClerks().toPromise()
    ]).then(([doctors, clerks]) => {
      this.doctors = doctors || [];
      this.clerks = clerks || [];
      this.isLoading = false;
    }).catch(err => {
      console.error('Error loading staff', err);
      this.errorMessage = 'Failed to load staff directory.';
      this.isLoading = false;
    });
  }

  onSubmitDoctor(): void {
    this.isSubmitting = true;
    this.doctorService.addDoctor(this.newDoctor).subscribe({
      next: () => {
        this.successMessage = 'Doctor registered successfully!';
        this.resetDoctorForm();
        this.loadStaff();
        this.isSubmitting = false;
      },
      error: () => {
        this.errorMessage = 'Failed to register doctor.';
        this.isSubmitting = false;
      }
    });
  }

  onSubmitClerk(): void {
    this.isSubmitting = true;
    this.clerkService.addClerk(this.newClerk).subscribe({
      next: () => {
        this.successMessage = 'Clerk registered successfully!';
        this.resetClerkForm();
        this.loadStaff();
        this.isSubmitting = false;
      },
      error: () => {
        this.errorMessage = 'Failed to register clerk.';
        this.isSubmitting = false;
      }
    });
  }

  resetDoctorForm(): void {
    this.newDoctor = { name: '', specialty: '', department: '', contactNumber: '', email: '', address: '', bio: '', profileImageUrl: '' };
    this.fileName = '';
  }

  resetClerkForm(): void {
    this.newClerk = { name: '', email: '', contactNumber: '', department: '', profileImageUrl: '' };
    this.fileName = '';
  }

  // Soft Delete
  deactivateDoctor(id: number): void {
    if (confirm('Deactivate this doctor? They will no longer be able to log in.')) {
      this.doctorService.deleteDoctor(id).subscribe(() => {
        this.successMessage = 'Doctor deactivated.';
        this.loadStaff();
      });
    }
  }

  deactivateClerk(id: number): void {
    if (confirm('Deactivate this clerk? They will no longer be able to log in.')) {
      this.clerkService.deleteClerk(id).subscribe(() => {
        this.successMessage = 'Clerk deactivated.';
        this.loadStaff();
      });
    }
  }

  // Patient Search Engine
  onSearchPatients(): void {
    if (!this.searchName && !this.searchPatientId) return;
    this.patientService.searchPatients(this.searchName, this.searchPatientId).subscribe(data => {
      this.searchResults = data;
      if (data.length === 0) {
        alert('No patients found matching your search.');
      }
    });
  }

  viewPatientHistory(patient: any): void {
    this.selectedPatient = patient;
    this.showPatientHistoryModal = true;
  }

  // Doctor Overview
   viewDoctorOverview(doctor: any): void {
     this.overviewDoctor = doctor;
     this.showOverviewModal = true;
   }

   logout(): void {
     this.authService.logout();
     this.router.navigate(['/login']);
   }

   onFileSelected(event: any): void {
     const file: File = event.target.files[0];
     if (file) {
       this.fileName = file.name;
       const reader = new FileReader();
       reader.onload = (e: any) => {
         if (this.activeTab === 'doctors') {
           this.newDoctor.profileImageUrl = e.target.result;
         } else {
           this.newClerk.profileImageUrl = e.target.result;
         }
       };
       reader.readAsDataURL(file);
     }
   }

   // Modal Methods
   openEditModal(staff: any, type: 'doctor' | 'clerk'): void {
     this.editingStaff = { ...staff };
     this.staffType = type;
     this.showEditModal = true;
   }

   closeEditModal(): void {
     this.showEditModal = false;
     this.editingStaff = null;
   }

   onUpdateStaff(): void {
     if (!this.editingStaff) return;
     this.isSubmitting = true;
     const serviceCall = this.staffType === 'doctor' 
       ? this.doctorService.updateDoctor(this.editingStaff.id, this.editingStaff)
       : this.clerkService.updateClerk(this.editingStaff.id, this.editingStaff);

     serviceCall.subscribe({
       next: () => {
         this.successMessage = `${this.staffType === 'doctor' ? 'Doctor' : 'Clerk'} updated successfully!`;
         this.closeEditModal();
         this.loadStaff();
         this.isSubmitting = false;
       },
       error: () => {
         this.errorMessage = 'Failed to update details.';
         this.isSubmitting = false;
       }
     });
   }
}
