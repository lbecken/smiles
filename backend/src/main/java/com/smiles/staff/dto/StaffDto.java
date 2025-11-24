package com.smiles.staff.dto;

import com.smiles.staff.domain.StaffRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for Staff entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {
    private UUID id;
    private UUID facilityId;
    private String keycloakUserId;
    private String name;
    private String email;
    private StaffRole role;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
