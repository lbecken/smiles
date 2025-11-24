package com.smiles.patients.service;

import com.smiles.common.security.SecurityUtils;
import com.smiles.patients.domain.Patient;
import com.smiles.patients.dto.CreatePatientRequest;
import com.smiles.patients.dto.PatientDto;
import com.smiles.patients.dto.UpdatePatientRequest;
import com.smiles.patients.mapper.PatientMapper;
import com.smiles.patients.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing patients.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final SecurityUtils securityUtils;

    /**
     * Get all patients for a facility.
     */
    public List<PatientDto> getPatientsByFacility(UUID facilityId) {
        log.debug("Getting patients for facility: {}", facilityId);

        // Check access permission
        securityUtils.checkFacilityAccess(facilityId);

        return patientRepository.findByFacilityId(facilityId).stream()
                .map(patientMapper::toDto)
                .toList();
    }

    /**
     * Get patient by ID.
     */
    public PatientDto getPatientById(UUID id) {
        log.debug("Getting patient by id: {}", id);
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(patient.getFacilityId());

        return patientMapper.toDto(patient);
    }

    /**
     * Get patient by Keycloak user ID.
     */
    public PatientDto getPatientByKeycloakUserId(String keycloakUserId) {
        log.debug("Getting patient by Keycloak user ID: {}", keycloakUserId);
        Patient patient = patientRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with Keycloak user ID: " + keycloakUserId));

        return patientMapper.toDto(patient);
    }

    /**
     * Create a new patient.
     */
    @Transactional
    public PatientDto createPatient(CreatePatientRequest request) {
        log.debug("Creating patient: {} for facility: {}", request.getName(), request.getFacilityId());

        // Check access permission
        securityUtils.checkFacilityAccess(request.getFacilityId());

        if (request.getKeycloakUserId() != null && patientRepository.existsByKeycloakUserId(request.getKeycloakUserId())) {
            throw new IllegalArgumentException("Patient already exists with Keycloak user ID: " + request.getKeycloakUserId());
        }

        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        log.info("Created patient: {} with id: {}", savedPatient.getName(), savedPatient.getId());

        return patientMapper.toDto(savedPatient);
    }

    /**
     * Update an existing patient.
     */
    @Transactional
    public PatientDto updatePatient(UUID id, UpdatePatientRequest request) {
        log.debug("Updating patient with id: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(patient.getFacilityId());

        patientMapper.updateEntityFromDto(request, patient);
        Patient updatedPatient = patientRepository.save(patient);
        log.info("Updated patient with id: {}", id);

        return patientMapper.toDto(updatedPatient);
    }

    /**
     * Delete a patient.
     */
    @Transactional
    public void deletePatient(UUID id) {
        log.debug("Deleting patient with id: {}", id);

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(patient.getFacilityId());

        patientRepository.deleteById(id);
        log.info("Deleted patient with id: {}", id);
    }

    /**
     * Link a Keycloak user to a patient.
     */
    @Transactional
    public PatientDto linkKeycloakUser(UUID patientId, String keycloakUserId) {
        log.debug("Linking Keycloak user {} to patient {}", keycloakUserId, patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id: " + patientId));

        // Check access permission
        securityUtils.checkFacilityAccess(patient.getFacilityId());

        if (patientRepository.existsByKeycloakUserId(keycloakUserId)) {
            throw new IllegalArgumentException("Keycloak user ID already linked to another patient");
        }

        patient.setKeycloakUserId(keycloakUserId);
        Patient updatedPatient = patientRepository.save(patient);
        log.info("Linked Keycloak user {} to patient {}", keycloakUserId, patientId);

        return patientMapper.toDto(updatedPatient);
    }
}
