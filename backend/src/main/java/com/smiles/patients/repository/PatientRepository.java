package com.smiles.patients.repository;

import com.smiles.patients.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Patient entity.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    /**
     * Find all patients for a facility.
     */
    List<Patient> findByFacilityId(UUID facilityId);

    /**
     * Find all active patients for a facility.
     */
    List<Patient> findByFacilityIdAndActiveTrue(UUID facilityId);

    /**
     * Find patient by Keycloak user ID.
     */
    Optional<Patient> findByKeycloakUserId(String keycloakUserId);

    /**
     * Find patient by email.
     */
    Optional<Patient> findByEmail(String email);

    /**
     * Check if patient exists by Keycloak user ID.
     */
    boolean existsByKeycloakUserId(String keycloakUserId);
}
