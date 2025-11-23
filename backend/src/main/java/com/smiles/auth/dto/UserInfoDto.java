package com.smiles.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO for current user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {

    /**
     * Unique user identifier (subject from JWT).
     */
    private String userId;

    /**
     * Username (preferred_username from Keycloak).
     */
    private String username;

    /**
     * User's email address.
     */
    private String email;

    /**
     * User's first name.
     */
    private String firstName;

    /**
     * User's last name.
     */
    private String lastName;

    /**
     * Full name (combination of first and last name).
     */
    private String fullName;

    /**
     * List of roles assigned to the user.
     */
    private List<String> roles;

    /**
     * Whether the email is verified.
     */
    private Boolean emailVerified;

    /**
     * Additional custom attributes from Keycloak.
     */
    private Map<String, Object> attributes;

    /**
     * Timestamp when the token was issued.
     */
    private Long issuedAt;

    /**
     * Timestamp when the token expires.
     */
    private Long expiresAt;
}
