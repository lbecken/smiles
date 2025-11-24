package com.smiles.appointments.dto;

import com.smiles.appointments.domain.AppointmentStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for Appointment entity.
 */
public record AppointmentDto(
    UUID id,
    UUID patientId,
    UUID dentistId,
    UUID roomId,
    UUID facilityId,
    Instant startTime,
    Instant endTime,
    AppointmentStatus status,
    Instant createdAt,
    Instant updatedAt
) {
}
