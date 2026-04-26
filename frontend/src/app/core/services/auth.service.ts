import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
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
  readonly isAuthenticated = computed(() => Boolean(this.sessionSignal()?.token));
  readonly role = computed<UserRole | null>(() => this.sessionSignal()?.role ?? null);
  readonly username = computed(() => this.sessionSignal()?.username ?? '');
  readonly isAdmin = computed(() => this.role() === 'ADMIN');

  login(credentials: AuthRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(ApiRoutes.auth.login, credentials).pipe(
      tap((response) => this.storeSession(response))
    );
  }

  register(credentials: AuthRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(ApiRoutes.auth.register, credentials).pipe(
      tap((response) => this.storeSession(response))
    );
  }

  getCurrentProfile(): Observable<UserProfile> {
    return this.api.get<UserProfile>(ApiRoutes.auth.me);
  }

  updateCurrentProfile(payload: ProfileUpdateRequest): Observable<UserProfile> {
    return this.api.put<UserProfile>(ApiRoutes.auth.me, payload);
  }

  logout(): void {
    localStorage.removeItem(SESSION_KEY);
    this.sessionSignal.set(null);
    void this.router.navigate(['/login']);
  }

  token(): string | null {
    return this.sessionSignal()?.token ?? null;
  }

  private storeSession(response: AuthResponse): void {
    const session: SessionState = {
      token: response.token,
      username: response.username,
      role: response.role
    };
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this.sessionSignal.set(session);
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
