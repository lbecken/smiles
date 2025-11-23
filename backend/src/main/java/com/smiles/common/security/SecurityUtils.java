package com.smiles.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for security-related operations.
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Get the current authenticated user's JWT token.
     *
     * @return the JWT token or null if not authenticated
     */
    public static Jwt getCurrentUserJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }

        return null;
    }

    /**
     * Get the current authenticated user's username.
     *
     * @return the username or null if not authenticated
     */
    public static String getCurrentUsername() {
        Jwt jwt = getCurrentUserJwt();
        return jwt != null ? jwt.getClaimAsString("preferred_username") : null;
    }

    /**
     * Get the current authenticated user's email.
     *
     * @return the email or null if not authenticated
     */
    public static String getCurrentUserEmail() {
        Jwt jwt = getCurrentUserJwt();
        return jwt != null ? jwt.getClaimAsString("email") : null;
    }

    /**
     * Get the current authenticated user's roles.
     *
     * @return list of roles
     */
    public static List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return authentication.getAuthorities().stream()
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
    public static boolean hasRole(String role) {
        return getCurrentUserRoles().contains(role);
    }

    /**
     * Get the current authenticated user's subject (unique identifier).
     *
     * @return the subject or null if not authenticated
     */
    public static String getCurrentUserSubject() {
        Jwt jwt = getCurrentUserJwt();
        return jwt != null ? jwt.getSubject() : null;
    }
}
