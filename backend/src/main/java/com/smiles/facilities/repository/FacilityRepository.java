package com.smiles.facilities.repository;

import com.smiles.facilities.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Facility entity.
 */
@Repository
public interface FacilityRepository extends JpaRepository<Facility, UUID> {

    /**
     * Find facility by name.
     */
    Optional<Facility> findByName(String name);

    /**
     * Check if facility exists by name.
     */
    boolean existsByName(String name);
}
