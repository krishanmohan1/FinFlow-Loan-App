import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthService } from '@core/services/auth.service';

export const refreshTokenInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const isAuthRefreshCall = request.url.includes('/auth/refresh');
  const isAuthLoginCall = request.url.includes('/auth/login');
  const isAuthRegisterCall = request.url.includes('/auth/register');
  const refreshAlreadyAttempted = request.headers.has('X-Refresh-Attempted');

  return next(request).pipe(
    catchError((error: unknown) => {
      if (
        !(error instanceof HttpErrorResponse) ||
        error.status !== 401 ||
        isAuthRefreshCall ||
        isAuthLoginCall ||
        isAuthRegisterCall ||
        refreshAlreadyAttempted ||
        !authService.isAuthenticated()
      ) {
        return throwError(() => error);
      }

      return authService.refreshSession().pipe(
        switchMap((response) =>
          next(
            request.clone({
              setHeaders: {
                Authorization: `Bearer ${response.accessToken}`,
                'X-Refresh-Attempted': 'true'
              }
            })
          )
        ),
        catchError((refreshError) => {
          authService.clearSession();
          return throwError(() => refreshError);
        })
      );
    })
  );
};
