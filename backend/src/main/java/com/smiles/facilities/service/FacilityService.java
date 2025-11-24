package com.smiles.facilities.service;

import com.smiles.facilities.domain.Facility;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.facilities.dto.FacilityDto;
import com.smiles.facilities.dto.UpdateFacilityRequest;
import com.smiles.facilities.mapper.FacilityMapper;
import com.smiles.facilities.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing facilities.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    /**
     * Get all facilities.
     */
    public List<FacilityDto> getAllFacilities() {
        log.debug("Getting all facilities");
        return facilityRepository.findAll().stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    /**
     * Get facility by ID.
     */
    public FacilityDto getFacilityById(UUID id) {
        log.debug("Getting facility by id: {}", id);
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + id));
        return facilityMapper.toDto(facility);
    }

    /**
     * Create a new facility.
     */
    @Transactional
    public FacilityDto createFacility(CreateFacilityRequest request) {
        log.debug("Creating facility: {}", request.getName());

        if (facilityRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Facility already exists with name: " + request.getName());
        }

        Facility facility = facilityMapper.toEntity(request);
        Facility savedFacility = facilityRepository.save(facility);
        log.info("Created facility: {} with id: {}", savedFacility.getName(), savedFacility.getId());

        return facilityMapper.toDto(savedFacility);
    }

    /**
     * Update an existing facility.
     */
    @Transactional
    public FacilityDto updateFacility(UUID id, UpdateFacilityRequest request) {
        log.debug("Updating facility with id: {}", id);

        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facility not found with id: " + id));

        // Check if name is being changed and if it conflicts
        if (request.getName() != null && !request.getName().equals(facility.getName())) {
            if (facilityRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Facility already exists with name: " + request.getName());
            }
        }

        facilityMapper.updateEntityFromDto(request, facility);
        Facility updatedFacility = facilityRepository.save(facility);
        log.info("Updated facility with id: {}", id);

        return facilityMapper.toDto(updatedFacility);
    }

    /**
     * Delete a facility.
     */
    @Transactional
    public void deleteFacility(UUID id) {
        log.debug("Deleting facility with id: {}", id);

        if (!facilityRepository.existsById(id)) {
            throw new IllegalArgumentException("Facility not found with id: " + id);
        }

        facilityRepository.deleteById(id);
        log.info("Deleted facility with id: {}", id);
    }
}
