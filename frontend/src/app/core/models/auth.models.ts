export type UserRole = 'USER' | 'ADMIN';

export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  role: UserRole;
  message: string;
}

export interface UserProfile {
  id: number;
  username: string;
  role: UserRole;
  createdAt: string;
  active: boolean;
}

export interface UserUpdateRequest {
  role?: UserRole;
  active?: boolean;
}

export interface SessionState {
  token: string;
  username: string;
  role: UserRole;
}
