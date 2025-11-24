package com.smiles.common.security;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Component to check facility access for users.
 * This is placed in the common module to avoid circular dependencies.
 */
@Component
public class FacilityAccessChecker {

    private org.springframework.context.ApplicationContext applicationContext;

    public FacilityAccessChecker(org.springframework.context.ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Check if a user has access to a facility.
     * Uses lazy loading of StaffRepository to avoid circular dependency.
     *
     * @param keycloakUserId the Keycloak user ID
     * @param facilityId the facility ID
     * @return true if the user has access, false otherwise
     */
    public boolean hasAccessToFacility(String keycloakUserId, UUID facilityId) {
        try {
            // Lazy load the StaffRepository to avoid circular dependency
            var staffRepository = applicationContext.getBean("staffRepository", org.springframework.data.jpa.repository.JpaRepository.class);

            // Find staff by keycloak user ID
            var method = staffRepository.getClass().getMethod("findByKeycloakUserId", String.class);
            var staffOptional = (java.util.Optional<?>) method.invoke(staffRepository, keycloakUserId);

            if (staffOptional.isPresent()) {
                var staff = staffOptional.get();
                var getFacilityId = staff.getClass().getMethod("getFacilityId");
                UUID staffFacilityId = (UUID) getFacilityId.invoke(staff);
                return facilityId.equals(staffFacilityId);
            }

            return false;
        } catch (Exception e) {
            // If there's any error, deny access
            return false;
        }
    }
}
