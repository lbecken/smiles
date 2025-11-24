import apiClient from './api';
import type { Appointment, CreateAppointmentRequest, UpdateAppointmentRequest } from '@/types/appointment';

/**
 * Service for appointment-related API calls.
 */
export const appointmentService = {
  /**
   * Get all appointments for a facility.
   */
  async getAppointmentsByFacility(
    facilityId: string,
    startTime?: string,
    endTime?: string
  ): Promise<Appointment[]> {
    const params: Record<string, string> = { facilityId };
    if (startTime) params.startTime = startTime;
    if (endTime) params.endTime = endTime;

    const response = await apiClient.get<Appointment[]>('/appointments', { params });
    return response.data;
  },

  /**
   * Get appointments by patient ID.
   */
  async getAppointmentsByPatient(patientId: string): Promise<Appointment[]> {
    const response = await apiClient.get<Appointment[]>(`/appointments/patient/${patientId}`);
    return response.data;
  },

  /**
   * Get appointments by dentist ID.
   */
  async getAppointmentsByDentist(dentistId: string): Promise<Appointment[]> {
    const response = await apiClient.get<Appointment[]>(`/appointments/dentist/${dentistId}`);
    return response.data;
  },

  /**
   * Get a single appointment by ID.
   */
  async getAppointment(id: string): Promise<Appointment> {
    const response = await apiClient.get<Appointment>(`/appointments/${id}`);
    return response.data;
  },

  /**
   * Create a new appointment.
   */
  async createAppointment(request: CreateAppointmentRequest): Promise<Appointment> {
    const response = await apiClient.post<Appointment>('/appointments', request);
    return response.data;
  },

  /**
   * Update an existing appointment.
   */
  async updateAppointment(id: string, request: UpdateAppointmentRequest): Promise<Appointment> {
    const response = await apiClient.put<Appointment>(`/appointments/${id}`, request);
    return response.data;
  },

  /**
   * Cancel an appointment.
   */
  async cancelAppointment(id: string): Promise<Appointment> {
    const response = await apiClient.post<Appointment>(`/appointments/${id}/cancel`);
    return response.data;
  },

  /**
   * Delete an appointment.
   */
  async deleteAppointment(id: string): Promise<void> {
    await apiClient.delete(`/appointments/${id}`);
  }
};
