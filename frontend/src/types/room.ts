/**
 * Room types.
 */
export enum RoomType {
  CHAIR = 'CHAIR',
  SURGERY_ROOM = 'SURGERY_ROOM'
}

/**
 * Room entity representing a treatment room or operatory.
 */
export interface Room {
  id: string;
  facilityId: string;
  name: string;
  type: RoomType;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request DTO for creating a room.
 */
export interface CreateRoomRequest {
  facilityId: string;
  name: string;
  type: RoomType;
}

/**
 * Request DTO for updating a room.
 */
export interface UpdateRoomRequest {
  name?: string;
  type?: RoomType;
}
