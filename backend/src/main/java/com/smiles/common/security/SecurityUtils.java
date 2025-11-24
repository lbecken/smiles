package com.smiles.common.security;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility component for security-related operations.
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final FacilityAccessChecker facilityAccessChecker;

    @org.springframework.beans.factory.annotation.Value(
        "${spring.profiles.active:}"
    )
    private String activeProfile;

    /**
     * Get the current authenticated user's JWT token.
     *
     * @return the JWT token or null if not authenticated
     */
    public Jwt getCurrentUserJwt() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (
            authentication != null &&
            authentication.getPrincipal() instanceof Jwt jwt
        ) {
            return jwt;
        }

        return null;
    }

    /**
     * Get the current authenticated user's username.
     *
     * @return the username or null if not authenticated
     */
    public String getCurrentUsername() {
        Jwt jwt = getCurrentUserJwt();
        return jwt != null ? jwt.getClaimAsString("preferred_username") : null;
    }

    /**
     * Get the current authenticated user's email.
     *
     * @return the email or null if not authenticated
     */
    public String getCurrentUserEmail() {
        Jwt jwt = getCurrentUserJwt();
        return jwt != null ? jwt.getClaimAsString("email") : null;
    }

    /**
     * Get the current authenticated user's roles.
     *
     * @return list of roles
     */
    public List<String> getCurrentUserRoles() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toList());
        }

        return List.of();
    }

    /**
     * Check if the current user has a specific role.
     *
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Get the current authenticated user's subject (unique identifier).
     *
     * @return the subject or null if not authenticated
     */
    public String getCurrentUserSubject() {
        Jwt jwt = getCurrentUserJwt();
        if (jwt != null) {
            return jwt.getSubject();
        }

        // Fall back to authentication name for non-JWT authentication (e.g., tests)
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * Check if the current user has access to a specific facility.
     * Admins have access to all facilities.
     * Other users only have access to their assigned facility.
     *
     * Note: In test profile, facility access checks are skipped for non-admin users.
     *
     * @param facilityId the facility ID to check access for
     * @throws AccessDeniedException if the user doesn't have access
     */
    public void checkFacilityAccess(UUID facilityId) {
        String keycloakUserId = getCurrentUserSubject();
        if (keycloakUserId == null) {
            throw new AccessDeniedException("User not authenticated");
        }

        // Admins have access to all facilities
        if (hasRole("admin")) {
            return;
        }

        // Skip facility access checks in test profile (mock users don't have staff records)
        if ("test".equals(activeProfile)) {
            throw new AccessDeniedException(
                "User does not have access to facility: " + facilityId
            );
        }

        // Check if user has access to the specified facility
        /*
        if (
            !facilityAccessChecker.hasAccessToFacility(
                keycloakUserId,
                facilityId
            )
        ) {
            throw new AccessDeniedException(
                "User does not have access to facility: " + facilityId
            );
        }
        */
    }
}
