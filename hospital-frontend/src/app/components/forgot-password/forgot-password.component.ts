import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css'
})
export class ForgotPasswordComponent {
  email: string = '';
  otp: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  
  step: number = 1; // 1: Email, 2: OTP, 3: New Password
  isLoading: boolean = false;
  message: string = '';
  isError: boolean = false;

  constructor(private router: Router) {}

  sendOTP() {
    if (!this.email) return;
    this.isLoading = true;
    this.message = '';
    
    // Mocking backend call
    setTimeout(() => {
      this.isLoading = false;
      this.message = 'OTP sent to your email (Mocked).';
      this.isError = false;
      this.step = 2;
    }, 1500);
  }

  verifyOTP() {
    if (!this.otp || this.otp.length !== 6) {
      this.message = 'Please enter a valid 6-digit OTP.';
      this.isError = true;
      return;
    }
    this.isLoading = true;
    this.message = '';

    // Mocking backend call
    setTimeout(() => {
      this.isLoading = false;
      this.message = 'OTP Verified! Please set your new password.';
      this.isError = false;
      this.step = 3;
    }, 1000);
  }

  resetPassword() {
    if (this.newPassword !== this.confirmPassword) {
      this.message = 'Passwords do not match.';
      this.isError = true;
      return;
    }
    
    if (this.newPassword.length < 6) {
      this.message = 'Password must be at least 6 characters.';
      this.isError = true;
      return;
    }

    this.isLoading = true;
    this.message = '';

    // Mocking backend call
    setTimeout(() => {
      this.isLoading = false;
      this.message = 'Password reset successful! Redirecting to login...';
      this.isError = false;
      setTimeout(() => {
        this.router.navigate(['/login']);
      }, 2000);
    }, 1500);
  }
}
