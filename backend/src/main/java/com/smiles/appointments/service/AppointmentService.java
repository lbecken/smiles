package com.smiles.appointments.service;

import com.smiles.appointments.domain.Appointment;
import com.smiles.appointments.domain.AppointmentStatus;
import com.smiles.appointments.dto.AppointmentDto;
import com.smiles.appointments.dto.CreateAppointmentRequest;
import com.smiles.appointments.dto.UpdateAppointmentRequest;
import com.smiles.appointments.mapper.AppointmentMapper;
import com.smiles.appointments.repository.AppointmentRepository;
import com.smiles.rooms.repository.RoomRepository;
import com.smiles.staff.repository.StaffRepository;
import com.smiles.staff.domain.StaffRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final StaffRepository staffRepository;
    private final RoomRepository roomRepository;
    private final AppointmentMapper appointmentMapper;

    /**
     * Get all appointments for a facility.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByFacility(UUID facilityId) {
        return appointmentRepository.findByFacilityId(facilityId).stream()
            .map(appointmentMapper::toDto)
            .toList();
    }

    /**
     * Get appointments for a facility within a time range.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByFacilityAndTimeRange(
        UUID facilityId,
        Instant startTime,
        Instant endTime
    ) {
        return appointmentRepository.findByFacilityIdAndTimeRange(facilityId, startTime, endTime).stream()
            .map(appointmentMapper::toDto)
            .toList();
    }

    /**
     * Get all appointments for a patient.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByPatient(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(appointmentMapper::toDto)
            .toList();
    }

    /**
     * Get all appointments for a dentist.
     */
    @Transactional(readOnly = true)
    public List<AppointmentDto> getAppointmentsByDentist(UUID dentistId) {
        return appointmentRepository.findByDentistId(dentistId).stream()
            .map(appointmentMapper::toDto)
            .toList();
    }

    /**
     * Get a single appointment by ID.
     */
    @Transactional(readOnly = true)
    public AppointmentDto getAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));
        return appointmentMapper.toDto(appointment);
    }

    /**
     * Create a new appointment with conflict detection.
     */
    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        // Validate time range
        if (!request.startTime().isBefore(request.endTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Validate dentist exists and is a dentist
        var dentist = staffRepository.findById(request.dentistId())
            .orElseThrow(() -> new IllegalArgumentException("Dentist not found: " + request.dentistId()));

        if (dentist.getRole() != StaffRole.DENTIST) {
            throw new IllegalArgumentException("Staff member is not a dentist: " + request.dentistId());
        }

        // Validate room exists
        var room = roomRepository.findById(request.roomId())
            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.roomId()));

        // Validate that dentist belongs to the facility
        if (!dentist.getFacilityId().equals(request.facilityId())) {
            throw new IllegalArgumentException("Dentist does not belong to the specified facility");
        }

        // Validate that room belongs to the facility
        if (!room.getFacilityId().equals(request.facilityId())) {
            throw new IllegalArgumentException("Room does not belong to the specified facility");
        }

        // Check for dentist conflicts
        boolean dentistConflict = appointmentRepository.hasDentistConflict(
            request.dentistId(),
            request.startTime(),
            request.endTime(),
            UUID.fromString("00000000-0000-0000-0000-000000000000"), // No existing appointment to exclude
            AppointmentStatus.CANCELLED
        );

        if (dentistConflict) {
            throw new IllegalStateException("Dentist has a conflicting appointment at this time");
        }

        // Check for room conflicts
        boolean roomConflict = appointmentRepository.hasRoomConflict(
            request.roomId(),
            request.startTime(),
            request.endTime(),
            UUID.fromString("00000000-0000-0000-0000-000000000000"), // No existing appointment to exclude
            AppointmentStatus.CANCELLED
        );

        if (roomConflict) {
            throw new IllegalStateException("Room has a conflicting appointment at this time");
        }

        // Create the appointment
        Appointment appointment = appointmentMapper.toEntity(request);
        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(saved);
    }

    /**
     * Update an existing appointment with conflict detection.
     */
    @Transactional
    public AppointmentDto updateAppointment(UUID id, UpdateAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));

        // Update dentist if provided
        if (request.dentistId() != null) {
            var dentist = staffRepository.findById(request.dentistId())
                .orElseThrow(() -> new IllegalArgumentException("Dentist not found: " + request.dentistId()));

            if (dentist.getRole() != StaffRole.DENTIST) {
                throw new IllegalArgumentException("Staff member is not a dentist: " + request.dentistId());
            }

            if (!dentist.getFacilityId().equals(appointment.getFacilityId())) {
                throw new IllegalArgumentException("Dentist does not belong to the same facility");
            }

            appointment.setDentistId(request.dentistId());
        }

        // Update room if provided
        if (request.roomId() != null) {
            var room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.roomId()));

            if (!room.getFacilityId().equals(appointment.getFacilityId())) {
                throw new IllegalArgumentException("Room does not belong to the same facility");
            }

            appointment.setRoomId(request.roomId());
        }

        // Update time if provided
        if (request.startTime() != null) {
            appointment.setStartTime(request.startTime());
        }
        if (request.endTime() != null) {
            appointment.setEndTime(request.endTime());
        }

        // Validate time range
        if (!appointment.getStartTime().isBefore(appointment.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check for conflicts only if time or dentist/room changed
        if (request.dentistId() != null || request.startTime() != null || request.endTime() != null) {
            boolean dentistConflict = appointmentRepository.hasDentistConflict(
                appointment.getDentistId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getId(),
                AppointmentStatus.CANCELLED
            );

            if (dentistConflict) {
                throw new IllegalStateException("Dentist has a conflicting appointment at this time");
            }
        }

        if (request.roomId() != null || request.startTime() != null || request.endTime() != null) {
            boolean roomConflict = appointmentRepository.hasRoomConflict(
                appointment.getRoomId(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getId(),
                AppointmentStatus.CANCELLED
            );

            if (roomConflict) {
                throw new IllegalStateException("Room has a conflicting appointment at this time");
            }
        }

        // Update status if provided
        if (request.status() != null) {
            appointment.setStatus(request.status());
        }

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(saved);
    }

    /**
     * Cancel an appointment.
     */
    @Transactional
    public AppointmentDto cancelAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Appointment not found: " + id));

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toDto(saved);
    }

    /**
     * Delete an appointment.
     */
    @Transactional
    public void deleteAppointment(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new IllegalArgumentException("Appointment not found: " + id);
        }
        appointmentRepository.deleteById(id);
    }
}
