package com.smiles.facilities.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for Facility entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {
    private UUID id;
    private String name;
    private String city;
    private String address;
    private Instant createdAt;
    private Instant updatedAt;
}
