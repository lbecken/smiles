/**
 * Facility entity representing a dental clinic/facility.
 */
export interface Facility {
  id: string;
  name: string;
  city: string;
  address: string;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request DTO for creating a facility.
 */
export interface CreateFacilityRequest {
  name: string;
  city: string;
  address: string;
}

/**
 * Request DTO for updating a facility.
 */
export interface UpdateFacilityRequest {
  name?: string;
  city?: string;
  address?: string;
}
