/**
 * Patient entity representing a patient.
 */
export interface Patient {
  id: string;
  facilityId: string;
  keycloakUserId?: string;
  name: string;
  birthDate: string;
  email?: string;
  phone?: string;
  address?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request DTO for creating a patient.
 */
export interface CreatePatientRequest {
  facilityId: string;
  keycloakUserId?: string;
  name: string;
  birthDate: string;
  email?: string;
  phone?: string;
  address?: string;
  active?: boolean;
}

/**
 * Request DTO for updating a patient.
 */
export interface UpdatePatientRequest {
  name?: string;
  birthDate?: string;
  email?: string;
  phone?: string;
  address?: string;
  active?: boolean;
}
