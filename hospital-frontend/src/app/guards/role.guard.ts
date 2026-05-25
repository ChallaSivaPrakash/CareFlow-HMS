import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

export const roleGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = localStorage.getItem('jwt_token');

  if (!token) {
    router.navigate(['http://localhost:4200/login']);
    return false;
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const userRole = payload.role; // Assumes payload has a 'role' field
    const expectedRole = route.data['expectedRole'];

    if (userRole === expectedRole || (Array.isArray(userRole) && userRole.includes(expectedRole))) {
      return true;
    }

    // Redirect to a default dashboard if unauthorized
    router.navigate(['http://localhost:4200/login']);
    return false;
  } catch (e) {
    router.navigate(['http://localhost:4200/login']);
    return false;
  }
};
