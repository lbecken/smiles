package com.smiles.staff.service;

import com.smiles.common.security.SecurityUtils;
import com.smiles.staff.domain.Staff;
import com.smiles.staff.dto.CreateStaffRequest;
import com.smiles.staff.dto.StaffDto;
import com.smiles.staff.dto.UpdateStaffRequest;
import com.smiles.staff.mapper.StaffMapper;
import com.smiles.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing staff members.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StaffService {

    private final StaffRepository staffRepository;
    private final StaffMapper staffMapper;
    private final SecurityUtils securityUtils;

    /**
     * Get all staff for a facility.
     */
    public List<StaffDto> getStaffByFacility(UUID facilityId) {
        log.debug("Getting staff for facility: {}", facilityId);

        // Check access permission
        securityUtils.checkFacilityAccess(facilityId);

        return staffRepository.findByFacilityId(facilityId).stream()
                .map(staffMapper::toDto)
                .toList();
    }

    /**
     * Get staff by ID.
     */
    public StaffDto getStaffById(UUID id) {
        log.debug("Getting staff by id: {}", id);
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(staff.getFacilityId());

        return staffMapper.toDto(staff);
    }

    /**
     * Get staff by Keycloak user ID.
     */
    public StaffDto getStaffByKeycloakUserId(String keycloakUserId) {
        log.debug("Getting staff by Keycloak user ID: {}", keycloakUserId);
        Staff staff = staffRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with Keycloak user ID: " + keycloakUserId));

        return staffMapper.toDto(staff);
    }

    /**
     * Create a new staff member.
     */
    @Transactional
    public StaffDto createStaff(CreateStaffRequest request) {
        log.debug("Creating staff: {} for facility: {}", request.getName(), request.getFacilityId());

        // Check access permission
        securityUtils.checkFacilityAccess(request.getFacilityId());

        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Staff already exists with email: " + request.getEmail());
        }

        if (request.getKeycloakUserId() != null && staffRepository.existsByKeycloakUserId(request.getKeycloakUserId())) {
            throw new IllegalArgumentException("Staff already exists with Keycloak user ID: " + request.getKeycloakUserId());
        }

        Staff staff = staffMapper.toEntity(request);
        Staff savedStaff = staffRepository.save(staff);
        log.info("Created staff: {} with id: {}", savedStaff.getName(), savedStaff.getId());

        return staffMapper.toDto(savedStaff);
    }

    /**
     * Update an existing staff member.
     */
    @Transactional
    public StaffDto updateStaff(UUID id, UpdateStaffRequest request) {
        log.debug("Updating staff with id: {}", id);

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(staff.getFacilityId());

        // Check if email is being changed and if it conflicts
        if (request.getEmail() != null && !request.getEmail().equals(staff.getEmail())) {
            if (staffRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Staff already exists with email: " + request.getEmail());
            }
        }

        staffMapper.updateEntityFromDto(request, staff);
        Staff updatedStaff = staffRepository.save(staff);
        log.info("Updated staff with id: {}", id);

        return staffMapper.toDto(updatedStaff);
    }

    /**
     * Delete a staff member.
     */
    @Transactional
    public void deleteStaff(UUID id) {
        log.debug("Deleting staff with id: {}", id);

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(staff.getFacilityId());

        staffRepository.deleteById(id);
        log.info("Deleted staff with id: {}", id);
    }

    /**
     * Link a Keycloak user to a staff member.
     */
    @Transactional
    public StaffDto linkKeycloakUser(UUID staffId, String keycloakUserId) {
        log.debug("Linking Keycloak user {} to staff {}", keycloakUserId, staffId);

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found with id: " + staffId));

        // Check access permission
        securityUtils.checkFacilityAccess(staff.getFacilityId());

        if (staffRepository.existsByKeycloakUserId(keycloakUserId)) {
            throw new IllegalArgumentException("Keycloak user ID already linked to another staff member");
        }

        staff.setKeycloakUserId(keycloakUserId);
        Staff updatedStaff = staffRepository.save(staff);
        log.info("Linked Keycloak user {} to staff {}", keycloakUserId, staffId);

        return staffMapper.toDto(updatedStaff);
    }
}
