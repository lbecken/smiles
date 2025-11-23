package com.smiles.auth.api;

import com.smiles.auth.dto.UserInfoDto;
import com.smiles.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication and user information endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    /**
     * Get current authenticated user information.
     *
     * @param authentication the authentication object
     * @return user information from JWT token
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoDto> getCurrentUser(Authentication authentication) {
        log.debug("Getting current user info for: {}", authentication.getName());

        Jwt jwt = SecurityUtils.getCurrentUserJwt();

        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        // Extract user information from JWT
        UserInfoDto userInfo = UserInfoDto.builder()
                .userId(jwt.getSubject())
                .username(jwt.getClaimAsString("preferred_username"))
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .fullName(jwt.getClaimAsString("name"))
                .roles(SecurityUtils.getCurrentUserRoles())
                .emailVerified(jwt.getClaimAsBoolean("email_verified"))
                .issuedAt(jwt.getIssuedAt() != null ? jwt.getIssuedAt().getEpochSecond() : null)
                .expiresAt(jwt.getExpiresAt() != null ? jwt.getExpiresAt().getEpochSecond() : null)
                .attributes(extractCustomAttributes(jwt))
                .build();

        log.debug("Returning user info for: {} with roles: {}", userInfo.getUsername(), userInfo.getRoles());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Health check endpoint to verify authentication is working.
     *
     * @return simple message with username
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> authHealth(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("authenticated", authentication != null && authentication.isAuthenticated());
        response.put("username", SecurityUtils.getCurrentUsername());
        response.put("roles", SecurityUtils.getCurrentUserRoles());
        response.put("timestamp", Instant.now().toString());

        return ResponseEntity.ok(response);
    }

    /**
     * Extract custom attributes from JWT claims.
     * This can include employee ID, patient ID, license numbers, etc.
     *
     * @param jwt the JWT token
     * @return map of custom attributes
     */
    private Map<String, Object> extractCustomAttributes(Jwt jwt) {
        Map<String, Object> attributes = new HashMap<>();

        // Extract common custom claims
        addIfPresent(attributes, jwt, "employeeId");
        addIfPresent(attributes, jwt, "patientId");
        addIfPresent(attributes, jwt, "licenseNumber");
        addIfPresent(attributes, jwt, "dateOfBirth");

        return attributes.isEmpty() ? null : attributes;
    }

    /**
     * Helper method to add claim to attributes map if present.
     */
    private void addIfPresent(Map<String, Object> attributes, Jwt jwt, String claimName) {
        Object value = jwt.getClaim(claimName);
        if (value != null) {
            attributes.put(claimName, value);
        }
    }
}
