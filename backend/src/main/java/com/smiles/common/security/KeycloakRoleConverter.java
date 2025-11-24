package com.smiles.common.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Converts Keycloak JWT roles to Spring Security authorities.
 *
 * Keycloak stores realm roles in the token under "realm_access.roles".
 * This converter extracts those roles and prefixes them with "ROLE_"
 * to align with Spring Security conventions.
 */
public class KeycloakRoleConverter
    implements Converter<Jwt, Collection<GrantedAuthority>> {

    //@Override
    public Collection<GrantedAuthority> __convert(Jwt jwt) {
        // Extract realm_access claim
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return Collections.emptyList();
        }

        // Extract roles list
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) realmAccess.get("roles");

        // Convert to GrantedAuthority with ROLE_ prefix
        return roles
            .stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Try to get roles from top-level "roles" claim first
        List<String> roles = jwt.getClaim("roles");

        // If not found at top level, try realm_access.roles (fallback)
        if (roles == null || roles.isEmpty()) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles")) {
                @SuppressWarnings("unchecked")
                List<String> castedRoles = (List<String>) realmAccess.get(
                    "roles"
                );
                roles = castedRoles;
            }
        }

        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        System.out.println("COnVERT");
        // Convert to GrantedAuthority with ROLE_ prefix
        return roles
            .stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
            .collect(Collectors.toList());
    }
}
