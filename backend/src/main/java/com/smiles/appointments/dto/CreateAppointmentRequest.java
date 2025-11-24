package com.smiles.appointments.dto;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Request DTO for creating a new appointment.
 */
public record CreateAppointmentRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Dentist ID is required")
    UUID dentistId,

    @NotNull(message = "Room ID is required")
    UUID roomId,

    @NotNull(message = "Facility ID is required")
    UUID facilityId,

    @NotNull(message = "Start time is required")
    Instant startTime,

    @NotNull(message = "End time is required")
    Instant endTime
) {
}
