package com.smiles.staff.dto;

import com.smiles.staff.domain.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a staff member.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStaffRequest {

    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    private StaffRole role;

    private Boolean active;
}
