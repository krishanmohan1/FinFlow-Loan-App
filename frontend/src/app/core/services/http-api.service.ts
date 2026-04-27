import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '@core/config/api.config';

export interface ApiRequestOptions {
  withCredentials?: boolean;
}

@Injectable({ providedIn: 'root' })
export class HttpApiService {
  private readonly http = inject(HttpClient);

  get<T>(path: string, options?: ApiRequestOptions): Observable<T> {
    return this.http.get<T>(this.url(path), this.httpOptions(options));
  }

  post<T>(path: string, body: unknown, options?: ApiRequestOptions): Observable<T> {
    return this.http.post<T>(this.url(path), body, this.httpOptions(options));
  }

  put<T>(path: string, body: unknown, options?: ApiRequestOptions): Observable<T> {
    return this.http.put<T>(this.url(path), body, this.httpOptions(options));
  }

  delete<T>(path: string, options?: ApiRequestOptions): Observable<T> {
    return this.http.delete<T>(this.url(path), this.httpOptions(options));
  }

  private url(path: string): string {
    return `${API_BASE_URL}${path}`;
  }

  private httpOptions(options?: ApiRequestOptions): { withCredentials?: boolean } {
    return options?.withCredentials ? { withCredentials: true } : {};
  }
}
