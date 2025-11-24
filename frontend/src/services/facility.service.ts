import type { Facility, CreateFacilityRequest, UpdateFacilityRequest } from '@/types/facility';
import apiClient from './api';

/**
 * Service for facility management.
 */
export const facilityService = {
  /**
   * Get all facilities (admin only).
   */
  async getAllFacilities(): Promise<Facility[]> {
    const response = await apiClient.get<Facility[]>('/facilities');
    return response.data;
  },

  /**
   * Get facility by ID.
   */
  async getFacilityById(id: string): Promise<Facility> {
    const response = await apiClient.get<Facility>(`/facilities/${id}`);
    return response.data;
  },

  /**
   * Create a new facility (admin only).
   */
  async createFacility(request: CreateFacilityRequest): Promise<Facility> {
    const response = await apiClient.post<Facility>('/facilities', request);
    return response.data;
  },

  /**
   * Update an existing facility (admin only).
   */
  async updateFacility(id: string, request: UpdateFacilityRequest): Promise<Facility> {
    const response = await apiClient.put<Facility>(`/facilities/${id}`, request);
    return response.data;
  },

  /**
   * Delete a facility (admin only).
   */
  async deleteFacility(id: string): Promise<void> {
    await apiClient.delete(`/facilities/${id}`);
  },
};
