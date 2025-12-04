export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  expiresIn: number;
  user: UserInformation;
}

// UserInformation from backend
export interface UserInformation {
  id: number;
  username: string;
  email: string;
  status: UserStatus;
  roles: string[];
}

export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface AuthState {
  user: UserInformation | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}
