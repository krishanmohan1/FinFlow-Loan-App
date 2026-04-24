import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, forkJoin, map, of } from 'rxjs';
import { API_BASE_URL } from '@core/config/api.config';
import { ServiceHealth } from '@core/models/health.models';

@Injectable({ providedIn: 'root' })
export class HealthService {
  private readonly http = inject(HttpClient);

  checkServices(): Observable<ServiceHealth[]> {
    const checks = [
      this.check('Auth', '/auth/test'),
      this.check('Application', '/application/all', true),
      this.check('Document', '/document/my', true),
      this.check('Admin', '/admin/reports', true)
    ];

    return forkJoin(checks);
  }

  private check(name: string, path: string, protectedEndpoint = false): Observable<ServiceHealth> {
    return this.http.get(`${API_BASE_URL}${path}`, { responseType: 'text' }).pipe(
      map(() => ({
        name,
        url: `${API_BASE_URL}${path}`,
        ok: true,
        detail: 'Reachable'
      })),
      catchError((error) =>
        of({
          name,
          url: `${API_BASE_URL}${path}`,
          ok: protectedEndpoint ? error.status === 401 || error.status === 403 : false,
          detail: protectedEndpoint && (error.status === 401 || error.status === 403)
            ? 'Reachable, needs login'
            : `Failed: ${error.status || 'network'}`
        })
      )
    );
  }
}
