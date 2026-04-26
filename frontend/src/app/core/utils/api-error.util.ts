export function resolveApiError(error: unknown, fallback: string): string {
  if (!error || typeof error !== 'object') {
    return fallback;
  }

  const httpError = error as {
    status?: number;
    error?: unknown;
    message?: string;
  };

  if (typeof httpError.error === 'string' && httpError.error.trim()) {
    return httpError.error;
  }

  if (httpError.error && typeof httpError.error === 'object') {
    const payload = httpError.error as {
      error?: string;
      message?: string;
      details?: Record<string, string>;
    };

    if (payload.details && Object.keys(payload.details).length > 0) {
      return Object.values(payload.details).join(' ');
    }

    if (payload.error?.trim()) {
      return payload.error;
    }

    if (payload.message?.trim()) {
      return payload.message;
    }
  }

  if (httpError.status === 0) {
    return 'Unable to reach FinFlow services. Confirm the frontend and backend are running together.';
  }

  return httpError.message?.trim() || fallback;
}
