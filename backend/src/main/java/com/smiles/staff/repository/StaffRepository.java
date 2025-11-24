package com.smiles.staff.repository;

import com.smiles.staff.domain.Staff;
import com.smiles.staff.domain.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Staff entity.
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    /**
     * Find all staff for a facility.
     */
    List<Staff> findByFacilityId(UUID facilityId);

    /**
     * Find all active staff for a facility.
     */
    List<Staff> findByFacilityIdAndActiveTrue(UUID facilityId);

    /**
     * Find staff by facility and role.
     */
    List<Staff> findByFacilityIdAndRole(UUID facilityId, StaffRole role);

    /**
     * Find staff by Keycloak user ID.
     */
    Optional<Staff> findByKeycloakUserId(String keycloakUserId);

    /**
     * Find staff by email.
     */
    Optional<Staff> findByEmail(String email);

    /**
     * Check if staff exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Check if staff exists by Keycloak user ID.
     */
    boolean existsByKeycloakUserId(String keycloakUserId);
}
