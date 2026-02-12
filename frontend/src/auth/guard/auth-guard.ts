import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth-service';
import { AuthMock } from '../mock/auth-mock';

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {

  const authService = inject(AuthMock);
  const router = inject(Router);

  const requiredRole: string | undefined = route.data?.['role'];

  if(!authService.isAuthenticated()){
    return router.createUrlTree(['']);
  }

  return true;
};
