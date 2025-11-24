import type { Staff, CreateStaffRequest, UpdateStaffRequest } from '@/types/staff';
import apiClient from './api';

/**
 * Service for staff management.
 */
export const staffService = {
  /**
   * Get all staff for a facility.
   */
  async getStaffByFacility(facilityId: string): Promise<Staff[]> {
    const response = await apiClient.get<Staff[]>('/staff', {
      params: { facilityId },
    });
    return response.data;
  },

  /**
   * Get staff by ID.
   */
  async getStaffById(id: string): Promise<Staff> {
    const response = await apiClient.get<Staff>(`/staff/${id}`);
    return response.data;
  },

  /**
   * Get staff by Keycloak user ID.
   */
  async getStaffByKeycloakUserId(keycloakUserId: string): Promise<Staff> {
    const response = await apiClient.get<Staff>(`/staff/by-keycloak/${keycloakUserId}`);
    return response.data;
  },

  /**
   * Create a new staff member (admin only).
   */
  async createStaff(request: CreateStaffRequest): Promise<Staff> {
    const response = await apiClient.post<Staff>('/staff', request);
    return response.data;
  },

  /**
   * Update an existing staff member (admin only).
   */
  async updateStaff(id: string, request: UpdateStaffRequest): Promise<Staff> {
    const response = await apiClient.put<Staff>(`/staff/${id}`, request);
    return response.data;
  },

  /**
   * Delete a staff member (admin only).
   */
  async deleteStaff(id: string): Promise<void> {
    await apiClient.delete(`/staff/${id}`);
  },

  /**
   * Link a Keycloak user to a staff member (admin only).
   */
  async linkKeycloakUser(id: string, keycloakUserId: string): Promise<Staff> {
    const response = await apiClient.post<Staff>(`/staff/${id}/link-keycloak/${keycloakUserId}`);
    return response.data;
  },
};
