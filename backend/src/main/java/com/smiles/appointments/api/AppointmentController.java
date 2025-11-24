package com.smiles.appointments.api;

import com.smiles.appointments.dto.AppointmentDto;
import com.smiles.appointments.dto.CreateAppointmentRequest;
import com.smiles.appointments.dto.UpdateAppointmentRequest;
import com.smiles.appointments.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for appointment management.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Get appointments by facility.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('DENTIST', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByFacility(
        @RequestParam UUID facilityId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime
    ) {
        if (startTime != null && endTime != null) {
            return ResponseEntity.ok(appointmentService.getAppointmentsByFacilityAndTimeRange(
                facilityId, startTime, endTime
            ));
        } else {
            return ResponseEntity.ok(appointmentService.getAppointmentsByFacility(facilityId));
        }
    }

    /**
     * Get appointments by patient.
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DENTIST', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    /**
     * Get appointments by dentist.
     */
    @GetMapping("/dentist/{dentistId}")
    @PreAuthorize("hasAnyRole('DENTIST', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDentist(@PathVariable UUID dentistId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDentist(dentistId));
    }

    /**
     * Get a single appointment by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DENTIST', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentDto> getAppointment(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointment(id));
    }

    /**
     * Create a new appointment.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        try {
            AppointmentDto created = appointmentService.createAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalStateException e) {
            // Conflict error (dentist or room double-booked)
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            // Validation error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Update an existing appointment.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentDto> updateAppointment(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        try {
            AppointmentDto updated = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            // Conflict error
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (IllegalArgumentException e) {
            // Validation or not found error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Cancel an appointment.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('PATIENT', 'RECEPTIONIST', 'ADMIN')")
    public ResponseEntity<AppointmentDto> cancelAppointment(@PathVariable UUID id) {
        try {
            AppointmentDto cancelled = appointmentService.cancelAppointment(id);
            return ResponseEntity.ok(cancelled);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Delete an appointment.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
