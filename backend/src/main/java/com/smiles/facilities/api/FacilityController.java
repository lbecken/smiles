package com.smiles.facilities.api;

import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.facilities.dto.FacilityDto;
import com.smiles.facilities.dto.UpdateFacilityRequest;
import com.smiles.facilities.service.FacilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for facility management.
 */
@Slf4j
@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    /**
     * Get all facilities (admin only).
     */
    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<FacilityDto>> getAllFacilities() {
        log.debug("GET /facilities - Get all facilities");
        List<FacilityDto> facilities = facilityService.getAllFacilities();
        return ResponseEntity.ok(facilities);
    }

    /**
     * Get facility by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FacilityDto> getFacilityById(@PathVariable UUID id) {
        log.debug("GET /facilities/{} - Get facility by ID", id);
        FacilityDto facility = facilityService.getFacilityById(id);
        return ResponseEntity.ok(facility);
    }

    /**
     * Create a new facility (admin only).
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<FacilityDto> createFacility(@Valid @RequestBody CreateFacilityRequest request) {
        log.debug("POST /facilities - Create facility: {}", request.getName());
        FacilityDto created = facilityService.createFacility(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing facility (admin only).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<FacilityDto> updateFacility(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFacilityRequest request) {
        log.debug("PUT /facilities/{} - Update facility", id);
        FacilityDto updated = facilityService.updateFacility(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a facility (admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteFacility(@PathVariable UUID id) {
        log.debug("DELETE /facilities/{} - Delete facility", id);
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }
}
