package com.smiles.facilities.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a facility.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFacilityRequest {

    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;
}
