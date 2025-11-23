/**
 * User information from the backend /api/auth/me endpoint.
 */
export interface UserInfo {
  userId: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  roles: string[];
  emailVerified?: boolean;
  attributes?: Record<string, any>;
  issuedAt?: number;
  expiresAt?: number;
}

/**
 * Authentication state.
 */
export interface AuthState {
  isAuthenticated: boolean;
  isLoading: boolean;
  user: UserInfo | null;
  token: string | null;
}

/**
 * Role types in the Smiles system.
 */
export type UserRole = 'ADMIN' | 'DENTIST' | 'STAFF' | 'PATIENT';

/**
 * Check if user has a specific role.
 */
export const hasRole = (user: UserInfo | null, role: UserRole): boolean => {
  return user?.roles?.includes(role) ?? false;
};

/**
 * Check if user has any of the specified roles.
 */
export const hasAnyRole = (user: UserInfo | null, roles: UserRole[]): boolean => {
  return roles.some((role) => hasRole(user, role));
};
