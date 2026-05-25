import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { DoctorService } from '../../services/doctor.service';
import { OpdClerkService } from '../../services/clerk.service';
import { PatientService } from '../../services/patient.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit {
  userProfile: any = {
    id: null,
    username: '',
    name: '',
    role: '',
    email: '',
    contactNumber: '',
    address: '',
    bio: '',
    profileImageUrl: ''
  };

  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
    otp: ''
  };

  isLoading = true;
  isSaving = false;
  successMessage = '';
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private doctorService: DoctorService,
    private clerkService: OpdClerkService,
    private patientService: PatientService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile(): void {
    const token = this.authService.getToken();
    if (!token) {
        this.router.navigate(['/login']);
        return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const role = payload.role || payload.roles;
      const username = payload.sub;

      if (role === 'ROLE_DOCTOR') {
        this.doctorService.getAllDoctors().subscribe(doctors => {
          const me = doctors.find(d => d.name.replaceAll(" ", "").toLowerCase() === username);
          if (me) this.userProfile = { ...me, role, username };
          this.isLoading = false;
        });
      } else if (role === 'ROLE_OPD_CLERK') {
        this.clerkService.getAllClerks().subscribe(clerks => {
          const me = clerks.find(c => c.name.replaceAll(" ", "").toLowerCase() === username);
          if (me) this.userProfile = { ...me, role, username };
          this.isLoading = false;
        });
      } else if (role === 'ROLE_PATIENT') {
        this.patientService.getMyMedicalRecord().subscribe(patient => {
          this.userProfile = { ...patient, role, username };
          this.isLoading = false;
        });
      } else {
        this.authService.getUserByUsername(username).subscribe(user => {
          this.userProfile = { ...user, role, username };
          this.isLoading = false;
        });
      }
    } catch (e) {
      this.errorMessage = 'Session expired or invalid. Please login again.';
      this.isLoading = false;
    }
  }

  // --- Image Upload Logic ---
  triggerImageUpload(): void {
    const input = document.getElementById('fileInput') as HTMLInputElement;
    input?.click();
  }

  handleFileInput(event: any): void {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.userProfile.profileImageUrl = e.target.result;
      };
      reader.readAsDataURL(file);
    }
  }

  onSaveProfile(): void {
    this.isSaving = true;
    this.successMessage = '';
    this.errorMessage = '';

    let updateObs;
    if (this.userProfile.role === 'ROLE_DOCTOR') {
      updateObs = this.doctorService.updateDoctor(this.userProfile.id, this.userProfile);
    } else if (this.userProfile.role === 'ROLE_OPD_CLERK') {
      updateObs = this.clerkService.updateClerk(this.userProfile.id, this.userProfile);
    } else if (this.userProfile.role === 'ROLE_PATIENT') {
      updateObs = this.patientService.updatePatient(this.userProfile.id, this.userProfile);
    } else if (this.userProfile.role === 'ROLE_ADMIN') {
      updateObs = this.authService.updateUser(this.userProfile.id, this.userProfile);
    }

    if (updateObs) {
      updateObs.subscribe({
        next: () => {
          this.successMessage = 'Profile updated successfully!';
          this.isSaving = false;
        },
        error: () => {
          this.errorMessage = 'Failed to update profile. Please try again.';
          this.isSaving = false;
        }
      });
    } else {
      this.errorMessage = 'Update operation not supported for this role.';
      this.isSaving = false;
    }
  }

  onChangePassword(): void {
    this.successMessage = 'Security verification: An OTP has been sent to your email. Please verify.';
  }
    
  goBack(): void {
    const role = this.userProfile.role;
    if (role === 'ROLE_ADMIN') this.router.navigate(['/admin-dashboard']);
    else if (role === 'ROLE_DOCTOR') this.router.navigate(['/doctor-dashboard']);
    // --- FIX THIS LINE BELOW ---
    else if (role === 'ROLE_OPD_CLERK') this.router.navigate(['/opd-dashboard']); 
    // ---------------------------
    else if (role === 'ROLE_PATIENT') this.router.navigate(['/patient-dashboard']);
    else this.router.navigate(['/login']);
  }
}