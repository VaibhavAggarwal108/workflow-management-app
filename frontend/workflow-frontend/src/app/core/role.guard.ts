import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const allowedRoles = route.data?.['roles'] as ('ADMIN' | 'MANAGER' | 'EMPLOYEE')[] | undefined;
  const currentUser = authService.getCurrentUser();

  if (!authService.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  if (!allowedRoles || allowedRoles.length === 0) {
    return true;
  }

  if (currentUser && allowedRoles.includes(currentUser.role)) {
    return true;
  }

  router.navigate(['/workflow-items']);
  return false;
};