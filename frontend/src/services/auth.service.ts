import apiClient from './api';
import type { UserInfo } from '@/types/auth';

/**
 * Auth service for authentication-related API calls.
 */
export const authService = {
  /**
   * Get current user information from the backend.
   */
  getCurrentUser: async (): Promise<UserInfo> => {
    const response = await apiClient.get<UserInfo>('/auth/me');
    return response.data;
  },

  /**
   * Check authentication health.
   */
  checkAuthHealth: async (): Promise<{ authenticated: boolean; username: string; roles: string[] }> => {
    const response = await apiClient.get('/auth/health');
    return response.data;
  },
};
