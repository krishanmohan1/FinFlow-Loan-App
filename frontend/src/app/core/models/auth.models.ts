export type UserRole = 'USER' | 'ADMIN';

export interface AuthRequest {
  username: string;
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  addressLine1?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  occupation?: string;
  annualIncome?: number;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  username: string;
  role: UserRole;
  message: string;
  accessTokenExpiresInMs: number;
}

export interface UserProfile {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: string;
  addressLine1: string;
  city: string;
  state: string;
  postalCode: string;
  occupation: string;
  annualIncome: number;
  role: UserRole;
  createdAt: string;
  active: boolean;
}

export interface UserUpdateRequest {
  role?: UserRole;
  active?: boolean;
}

export interface ProfileUpdateRequest {
  fullName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: string;
  addressLine1: string;
  city: string;
  state: string;
  postalCode: string;
  occupation: string;
  annualIncome: number;
}

export interface StaffRegistrationRequest extends AuthRequest {}

export interface SessionState {
  accessToken: string;
  username: string;
  role: UserRole;
}
