/**
 * Staff roles.
 */
export enum StaffRole {
  DENTIST = 'DENTIST',
  ASSISTANT = 'ASSISTANT',
  RECEPTIONIST = 'RECEPTIONIST',
  ADMIN = 'ADMIN'
}

/**
 * Staff entity representing a staff member.
 */
export interface Staff {
  id: string;
  facilityId: string;
  keycloakUserId?: string;
  name: string;
  email: string;
  role: StaffRole;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request DTO for creating a staff member.
 */
export interface CreateStaffRequest {
  facilityId: string;
  keycloakUserId?: string;
  name: string;
  email: string;
  role: StaffRole;
  active?: boolean;
}

/**
 * Request DTO for updating a staff member.
 */
export interface UpdateStaffRequest {
  name?: string;
  email?: string;
  role?: StaffRole;
  active?: boolean;
}
