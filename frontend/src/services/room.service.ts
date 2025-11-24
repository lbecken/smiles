import type { Room, CreateRoomRequest, UpdateRoomRequest } from '@/types/room';
import apiClient from './api';

/**
 * Service for room management.
 */
export const roomService = {
  /**
   * Get all rooms for a facility.
   */
  async getRoomsByFacility(facilityId: string): Promise<Room[]> {
    const response = await apiClient.get<Room[]>('/rooms', {
      params: { facilityId },
    });
    return response.data;
  },

  /**
   * Get room by ID.
   */
  async getRoomById(id: string): Promise<Room> {
    const response = await apiClient.get<Room>(`/rooms/${id}`);
    return response.data;
  },

  /**
   * Create a new room.
   */
  async createRoom(request: CreateRoomRequest): Promise<Room> {
    const response = await apiClient.post<Room>('/rooms', request);
    return response.data;
  },

  /**
   * Update an existing room.
   */
  async updateRoom(id: string, request: UpdateRoomRequest): Promise<Room> {
    const response = await apiClient.put<Room>(`/rooms/${id}`, request);
    return response.data;
  },

  /**
   * Delete a room (admin only).
   */
  async deleteRoom(id: string): Promise<void> {
    await apiClient.delete(`/rooms/${id}`);
  },
};
