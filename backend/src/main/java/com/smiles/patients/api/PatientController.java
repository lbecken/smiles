package com.smiles.patients.api;

import com.smiles.patients.dto.CreatePatientRequest;
import com.smiles.patients.dto.PatientDto;
import com.smiles.patients.dto.UpdatePatientRequest;
import com.smiles.patients.service.PatientService;
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
 * REST controller for patient management.
 */
@Slf4j
@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Get all patients for a facility.
     */
    @GetMapping
    public ResponseEntity<List<PatientDto>> getPatientsByFacility(@RequestParam UUID facilityId) {
        log.debug("GET /patients?facilityId={} - Get patients by facility", facilityId);
        List<PatientDto> patients = patientService.getPatientsByFacility(facilityId);
        return ResponseEntity.ok(patients);
    }

    /**
     * Get patient by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable UUID id) {
        log.debug("GET /patients/{} - Get patient by ID", id);
        PatientDto patient = patientService.getPatientById(id);
        return ResponseEntity.ok(patient);
    }

    /**
     * Get patient by Keycloak user ID.
     */
    @GetMapping("/by-keycloak/{keycloakUserId}")
    public ResponseEntity<PatientDto> getPatientByKeycloakUserId(@PathVariable String keycloakUserId) {
        log.debug("GET /patients/by-keycloak/{} - Get patient by Keycloak user ID", keycloakUserId);
        PatientDto patient = patientService.getPatientByKeycloakUserId(keycloakUserId);
        return ResponseEntity.ok(patient);
    }

    /**
     * Create a new patient (admin and receptionist).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'receptionist')")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        log.debug("POST /patients - Create patient: {}", request.getName());
        PatientDto created = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing patient (admin and receptionist).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'receptionist')")
    public ResponseEntity<PatientDto> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePatientRequest request) {
        log.debug("PUT /patients/{} - Update patient", id);
        PatientDto updated = patientService.updatePatient(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a patient (admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        log.debug("DELETE /patients/{} - Delete patient", id);
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Link a Keycloak user to a patient (admin and receptionist).
     */
    @PostMapping("/{id}/link-keycloak/{keycloakUserId}")
    @PreAuthorize("hasAnyRole('admin', 'receptionist')")
    public ResponseEntity<PatientDto> linkKeycloakUser(
            @PathVariable UUID id,
            @PathVariable String keycloakUserId) {
        log.debug("POST /patients/{}/link-keycloak/{} - Link Keycloak user to patient", id, keycloakUserId);
        PatientDto updated = patientService.linkKeycloakUser(id, keycloakUserId);
        return ResponseEntity.ok(updated);
    }
}
