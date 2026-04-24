import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '@core/services/auth.service';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const token = authService.token();
  const isPublicAuthRequest = request.url.includes('/auth/login') || request.url.includes('/auth/register');

  if (!token || isPublicAuthRequest) {
    return next(request);
  }

  const session = authService.session();

  return next(
    request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
        // In production the API Gateway injects these headers. In local Angular
        // development we also send them so direct service proxies work end-to-end.
        'X-Auth-Username': session?.username ?? '',
        'X-Auth-Role': session?.role ?? ''
      }
    })
  );
};
