package com.smiles.staff.api;

import com.smiles.staff.dto.CreateStaffRequest;
import com.smiles.staff.dto.StaffDto;
import com.smiles.staff.dto.UpdateStaffRequest;
import com.smiles.staff.service.StaffService;
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
 * REST controller for staff management.
 */
@Slf4j
@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    /**
     * Get all staff for a facility.
     */
    @GetMapping
    public ResponseEntity<List<StaffDto>> getStaffByFacility(@RequestParam UUID facilityId) {
        log.debug("GET /staff?facilityId={} - Get staff by facility", facilityId);
        List<StaffDto> staff = staffService.getStaffByFacility(facilityId);
        return ResponseEntity.ok(staff);
    }

    /**
     * Get staff by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable UUID id) {
        log.debug("GET /staff/{} - Get staff by ID", id);
        StaffDto staff = staffService.getStaffById(id);
        return ResponseEntity.ok(staff);
    }

    /**
     * Get staff by Keycloak user ID.
     */
    @GetMapping("/by-keycloak/{keycloakUserId}")
    public ResponseEntity<StaffDto> getStaffByKeycloakUserId(@PathVariable String keycloakUserId) {
        log.debug("GET /staff/by-keycloak/{} - Get staff by Keycloak user ID", keycloakUserId);
        StaffDto staff = staffService.getStaffByKeycloakUserId(keycloakUserId);
        return ResponseEntity.ok(staff);
    }

    /**
     * Create a new staff member (admin only).
     */
    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StaffDto> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        log.debug("POST /staff - Create staff: {}", request.getName());
        StaffDto created = staffService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing staff member (admin only).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StaffDto> updateStaff(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStaffRequest request) {
        log.debug("PUT /staff/{} - Update staff", id);
        StaffDto updated = staffService.updateStaff(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a staff member (admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID id) {
        log.debug("DELETE /staff/{} - Delete staff", id);
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Link a Keycloak user to a staff member (admin only).
     */
    @PostMapping("/{id}/link-keycloak/{keycloakUserId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<StaffDto> linkKeycloakUser(
            @PathVariable UUID id,
            @PathVariable String keycloakUserId) {
        log.debug("POST /staff/{}/link-keycloak/{} - Link Keycloak user to staff", id, keycloakUserId);
        StaffDto updated = staffService.linkKeycloakUser(id, keycloakUserId);
        return ResponseEntity.ok(updated);
    }
}
