package com.smiles.rooms.dto;

import com.smiles.rooms.domain.RoomType;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a room.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoomRequest {

    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    private RoomType type;
}
