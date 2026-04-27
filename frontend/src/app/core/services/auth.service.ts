import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { ApiRoutes } from '@core/config/api.config';
import { AuthRequest, AuthResponse, ProfileUpdateRequest, SessionState, UserProfile, UserRole } from '@core/models/auth.models';
import { HttpApiService } from './http-api.service';

const SESSION_KEY = 'finflow.session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(HttpApiService);
  private readonly router = inject(Router);
  private readonly sessionSignal = signal<SessionState | null>(this.readStoredSession());

  readonly session = this.sessionSignal.asReadonly();
  readonly isAuthenticated = computed(() => Boolean(this.sessionSignal()?.accessToken));
  readonly role = computed<UserRole | null>(() => this.sessionSignal()?.role ?? null);
  readonly username = computed(() => this.sessionSignal()?.username ?? '');
  readonly isAdmin = computed(() => this.role() === 'ADMIN');

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(ApiRoutes.auth.login, credentials, { withCredentials: true }).pipe(
      tap((response) => this.storeSession(response))
    );
  }

  register(credentials: AuthRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(ApiRoutes.auth.register, credentials, { withCredentials: true }).pipe(
      tap((response) => this.storeSession(response))
    );
  }

  refreshSession(): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(ApiRoutes.auth.refresh, {}, { withCredentials: true }).pipe(
      tap((response) => this.storeSession(response))
    );
  }

  bootstrapSession(): Observable<boolean> {
    if (this.sessionSignal()?.accessToken) {
      return of(true);
    }

    return this.refreshSession().pipe(
      map(() => true),
      catchError(() => {
        this.clearSession();
        return of(false);
      })
    );
  }

  getCurrentProfile(): Observable<UserProfile> {
    return this.api.get<UserProfile>(ApiRoutes.auth.me);
  }

  updateCurrentProfile(payload: ProfileUpdateRequest): Observable<UserProfile> {
    return this.api.put<UserProfile>(ApiRoutes.auth.me, payload);
  }

  logout(): void {
    this.api.post<string>(ApiRoutes.auth.logout, {}, { withCredentials: true }).subscribe({
      next: () => this.clearSessionAndRedirect(),
      error: () => this.clearSessionAndRedirect()
    });
  }

  token(): string | null {
    return this.sessionSignal()?.accessToken ?? null;
  }

  setSession(response: AuthResponse): void {
    this.storeSession(response);
  }

  clearSession(): void {
    localStorage.removeItem(SESSION_KEY);
    this.sessionSignal.set(null);
  }

  private storeSession(response: AuthResponse): void {
    const session: SessionState = {
      accessToken: response.accessToken,
      username: response.username,
      role: response.role
    };
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this.sessionSignal.set(session);
  }

  private clearSessionAndRedirect(): void {
    this.clearSession();
    void this.router.navigate(['/login']);
  }

  private readStoredSession(): SessionState | null {
    try {
      const raw = localStorage.getItem(SESSION_KEY);
      return raw ? (JSON.parse(raw) as SessionState) : null;
    } catch {
      localStorage.removeItem(SESSION_KEY);
      return null;
    }
  }
}
