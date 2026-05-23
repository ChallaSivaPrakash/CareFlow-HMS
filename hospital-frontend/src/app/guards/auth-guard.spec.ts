import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  
  // Check if the user has a token badge
  const token = localStorage.getItem('jwt_token');

  if (token) {
    return true; // Access Granted
  } else {
    // Access Denied: Send them back to the login page
    router.navigate(['/login']);
    return false;
  }
};