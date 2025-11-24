package com.smiles.patients.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for Patient entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientDto {
    private UUID id;
    private UUID facilityId;
    private String keycloakUserId;
    private String name;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String address;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}
