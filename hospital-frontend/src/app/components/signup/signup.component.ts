import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent {
  formData = {
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  };
  
  errorMessage: string = '';
  successMessage: string = '';
  isPasswordVisible: boolean = false;
  isConfirmPasswordVisible: boolean = false;
  
  // Password validation regex
  passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  constructor(private authService: AuthService, private router: Router) { }

  // Check each password requirement
  get hasMinLength() {
    return this.formData.password.length >= 8;
  }
  get hasUpperCase() {
    return /[A-Z]/.test(this.formData.password);
  }
  get hasLowerCase() {
    return /[a-z]/.test(this.formData.password);
  }
  get hasNumber() {
    return /\d/.test(this.formData.password);
  }
  get hasSpecialChar() {
    return /[@$!%*?&]/.test(this.formData.password);
  }
  get passwordsMatch() {
    return this.formData.password === this.formData.confirmPassword && this.formData.confirmPassword !== '';
  }
  get isFormValid() {
    return this.passwordRegex.test(this.formData.password) && 
           this.passwordsMatch && 
           this.formData.fullName.trim() !== '' && 
           this.formData.email.trim() !== '';
  }

  togglePassword() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  toggleConfirmPassword() {
    this.isConfirmPasswordVisible = !this.isConfirmPasswordVisible;
  }

  onSignup() {
    if (!this.isFormValid) {
      return;
    }
    
    this.errorMessage = '';
    this.successMessage = '';

    const registrationPayload = {
    name: this.formData.fullName,
    username: this.formData.email, // <--- Map email to username
    password: this.formData.password,
    role: 'ROLE_PATIENT'
  };
    
    this.authService.registerPatient({
      name: this.formData.fullName,
      username: this.formData.email,
      password: this.formData.password,
      role: 'ROLE_PATIENT'
    }).subscribe({
      next: (response: any) => {
        this.successMessage = 'Account created successfully! Please login.';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        // Handle Spring Boot validation errors or custom messages
        if (err.error) {
          if (typeof err.error === 'string') {
            this.errorMessage = err.error;
          } else if (Array.isArray(err.error)) {
            this.errorMessage = err.error.map((e: any) => e.defaultMessage || e.message).join(', ');
          } else if (typeof err.error === 'object') {
            // Handle map of field errors (from GlobalExceptionHandler)
            this.errorMessage = Object.entries(err.error)
              .map(([field, message]) => `${field}: ${message}`)
              .join(', ');
          } else {
            this.errorMessage = 'Failed to create account. Please try again.';
          }
        } else {
          this.errorMessage = 'Failed to create account. Please try again.';
        }
      }
    });
  }
}
