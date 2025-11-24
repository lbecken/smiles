package com.smiles.appointments.dto;

import com.smiles.appointments.domain.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

/**
 * Request DTO for updating an existing appointment.
 */
public record UpdateAppointmentRequest(
    UUID dentistId,
    UUID roomId,
    Instant startTime,
    Instant endTime,
    AppointmentStatus status
) {
}
