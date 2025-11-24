import type { Patient, CreatePatientRequest, UpdatePatientRequest } from '@/types/patient';
import apiClient from './api';

/**
 * Service for patient management.
 */
export const patientService = {
  /**
   * Get all patients for a facility.
   */
  async getPatientsByFacility(facilityId: string): Promise<Patient[]> {
    const response = await apiClient.get<Patient[]>('/patients', {
      params: { facilityId },
    });
    return response.data;
  },

  /**
   * Get patient by ID.
   */
  async getPatientById(id: string): Promise<Patient> {
    const response = await apiClient.get<Patient>(`/patients/${id}`);
    return response.data;
  },

  /**
   * Get patient by Keycloak user ID.
   */
  async getPatientByKeycloakUserId(keycloakUserId: string): Promise<Patient> {
    const response = await apiClient.get<Patient>(`/patients/by-keycloak/${keycloakUserId}`);
    return response.data;
  },

  /**
   * Create a new patient.
   */
  async createPatient(request: CreatePatientRequest): Promise<Patient> {
    const response = await apiClient.post<Patient>('/patients', request);
    return response.data;
  },

  /**
   * Update an existing patient.
   */
  async updatePatient(id: string, request: UpdatePatientRequest): Promise<Patient> {
    const response = await apiClient.put<Patient>(`/patients/${id}`, request);
    return response.data;
  },

  /**
   * Delete a patient (admin only).
   */
  async deletePatient(id: string): Promise<void> {
    await apiClient.delete(`/patients/${id}`);
  },

  /**
   * Link a Keycloak user to a patient.
   */
  async linkKeycloakUser(id: string, keycloakUserId: string): Promise<Patient> {
    const response = await apiClient.post<Patient>(`/patients/${id}/link-keycloak/${keycloakUserId}`);
    return response.data;
  },
};
