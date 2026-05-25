import { Routes } from '@angular/router';
import { PatientComponent } from './components/patient/patient.component';
import { LoginComponent } from './components/login/login.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { DoctorDashboardComponent } from './components/doctor-dashboard/doctor-dashboard.component';
import { PatientDashboardComponent } from './components/patient-dashboard/patient-dashboard.component';
import { authGuard } from './guards/auth-guard';
import { roleGuard } from './guards/role.guard';
import { ProfileComponent } from './components/profile/profile.component';
import { ForgotPasswordComponent } from './components/forgot-password/forgot-password.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { 
    path: 'opd-dashboard', 
    component: PatientComponent, 
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'ROLE_OPD_CLERK' }
  },
  { 
    path: 'admin-dashboard', 
    component: AdminDashboardComponent, 
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'ROLE_ADMIN' }
  },
  { 
    path: 'doctor-dashboard', 
    component: DoctorDashboardComponent, 
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'ROLE_DOCTOR' }
  },
  { 
    path: 'patient-dashboard', 
    component: PatientDashboardComponent, 
    canActivate: [authGuard, roleGuard],
    data: { expectedRole: 'ROLE_PATIENT' }
  },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' } // <-- THIS CATCHES BLANK SCREENS
];