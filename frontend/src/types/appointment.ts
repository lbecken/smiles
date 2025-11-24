/**
 * Appointment status.
 */
export const AppointmentStatus = {
  SCHEDULED: 'SCHEDULED',
  ONGOING: 'ONGOING',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED'
} as const;

export type AppointmentStatus = typeof AppointmentStatus[keyof typeof AppointmentStatus];

/**
 * Appointment entity representing a scheduled appointment.
 */
export interface Appointment {
  id: string;
  patientId: string;
  dentistId: string;
  roomId: string;
  facilityId: string;
  startTime: string;
  endTime: string;
  status: AppointmentStatus;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request DTO for creating an appointment.
 */
export interface CreateAppointmentRequest {
  patientId: string;
  dentistId: string;
  roomId: string;
  facilityId: string;
  startTime: string;
  endTime: string;
}

/**
 * Request DTO for updating an appointment.
 */
export interface UpdateAppointmentRequest {
  dentistId?: string;
  roomId?: string;
  startTime?: string;
  endTime?: string;
  status?: AppointmentStatus;
}
