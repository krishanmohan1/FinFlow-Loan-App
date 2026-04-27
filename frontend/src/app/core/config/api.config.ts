import { environment } from '@env/environment';

export const API_BASE_URL = environment.apiBaseUrl;

export const ApiRoutes = {
  auth: {
    login: '/auth/login',
    register: '/auth/register',
    refresh: '/auth/refresh',
    logout: '/auth/logout',
    me: '/auth/users/me',
    users: '/auth/users/all',
    userById: (id: number) => `/auth/users/${id}`,
    deactivate: (id: number) => `/auth/users/${id}/deactivate`
  },
  loans: {
    apply: '/application/apply',
    all: '/application/all',
    byId: (id: number) => `/application/${id}`,
    byStatus: (status: string) => `/application/status/${status}`,
    withdraw: (id: number) => `/application/${id}/withdraw`
  },
  documents: {
    upload: '/document/upload',
    mine: '/document/my',
    all: '/document/all',
    byId: (id: number) => `/document/${id}`,
    byLoan: (loanId: string) => `/document/loan/${loanId}`,
    verify: (id: number) => `/document/verify/${id}`
  },
  admin: {
    loans: '/admin/loans',
    staff: '/admin/staff',
    loanById: (id: number) => `/admin/loans/${id}`,
    loansByStatus: (status: string) => `/admin/loans/status/${status}`,
    loansByUser: (username: string) => `/admin/loans/user/${username}`,
    decision: (id: number) => `/admin/loans/${id}/decision`,
    approve: (id: number, remarks: string) => `/admin/loans/${id}/approve?remarks=${encodeURIComponent(remarks)}`,
    reject: (id: number, remarks: string) => `/admin/loans/${id}/reject?remarks=${encodeURIComponent(remarks)}`,
    review: (id: number) => `/admin/loans/${id}/review`,
    documents: '/admin/documents',
    documentById: (id: number) => `/admin/documents/${id}`,
    documentsByStatus: (status: string) => `/admin/documents/status/${status}`,
    verifyDocument: (id: number) => `/admin/documents/${id}/verify`,
    users: '/admin/users',
    userById: (id: number) => `/admin/users/${id}`,
    deactivateUser: (id: number) => `/admin/users/${id}/deactivate`,
    reports: '/admin/reports',
    counts: '/admin/reports/counts'
  }
} as const;
