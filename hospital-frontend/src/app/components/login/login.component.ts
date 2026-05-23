import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',       
  styleUrls: ['./login.css']         
})
export class LoginComponent {
  credentials = {
    username: '',
    password: ''
  };
  
  errorMessage: string = '';
  isPasswordVisible: boolean = false; // Tracks eye toggle state

  constructor(private authService: AuthService, private router: Router) {}

  // Flips the password visibility state
  togglePassword() {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onLogin() {
    this.errorMessage = ''; // clear previous errors
    
    this.authService.login(this.credentials).subscribe({
      next: (response: any) => {
        // Role-based redirection
        const role = response.role || this.authService.getUserRole();

        console.log("Login Successful! Detected Role: ", role); // Debugging log
        
        if (role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        } else if (role === 'ROLE_DOCTOR') {
          this.router.navigate(['/doctor-dashboard']);
        } else if (role === 'ROLE_PATIENT') {
          this.router.navigate(['/patient-dashboard']);
        } else if (role === 'ROLE_OPD_CLERK') {
          this.router.navigate(['/dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        this.errorMessage = 'Invalid username or password. Please try again.';
      }
    });
  }
}