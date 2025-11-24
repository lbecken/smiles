package com.smiles.rooms.dto;

import com.smiles.rooms.domain.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for Room entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private UUID id;
    private UUID facilityId;
    private String name;
    private RoomType type;
    private Instant createdAt;
    private Instant updatedAt;
}
