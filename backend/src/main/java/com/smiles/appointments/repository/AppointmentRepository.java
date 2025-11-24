package com.smiles.appointments.repository;

import com.smiles.appointments.domain.Appointment;
import com.smiles.appointments.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Find all appointments for a specific facility.
     */
    List<Appointment> findByFacilityId(UUID facilityId);

    /**
     * Find all appointments for a specific patient.
     */
    List<Appointment> findByPatientId(UUID patientId);

    /**
     * Find all appointments for a specific dentist.
     */
    List<Appointment> findByDentistId(UUID dentistId);

    /**
     * Find appointments for a facility within a time range.
     */
    @Query("SELECT a FROM Appointment a WHERE a.facilityId = :facilityId " +
           "AND a.startTime < :endTime AND a.endTime > :startTime " +
           "ORDER BY a.startTime")
    List<Appointment> findByFacilityIdAndTimeRange(
        @Param("facilityId") UUID facilityId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime
    );

    /**
     * Find appointments for a dentist within a time range (excluding cancelled).
     */
    @Query("SELECT a FROM Appointment a WHERE a.dentistId = :dentistId " +
           "AND a.status != :cancelledStatus " +
           "AND a.startTime < :endTime AND a.endTime > :startTime " +
           "ORDER BY a.startTime")
    List<Appointment> findByDentistIdAndTimeRangeExcludingCancelled(
        @Param("dentistId") UUID dentistId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    /**
     * Find appointments for a room within a time range (excluding cancelled).
     */
    @Query("SELECT a FROM Appointment a WHERE a.roomId = :roomId " +
           "AND a.status != :cancelledStatus " +
           "AND a.startTime < :endTime AND a.endTime > :startTime " +
           "ORDER BY a.startTime")
    List<Appointment> findByRoomIdAndTimeRangeExcludingCancelled(
        @Param("roomId") UUID roomId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    /**
     * Check if dentist has conflicts (excluding a specific appointment ID and cancelled appointments).
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.dentistId = :dentistId " +
           "AND a.id != :excludeId " +
           "AND a.status != :cancelledStatus " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean hasDentistConflict(
        @Param("dentistId") UUID dentistId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("excludeId") UUID excludeId,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );

    /**
     * Check if room has conflicts (excluding a specific appointment ID and cancelled appointments).
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.roomId = :roomId " +
           "AND a.id != :excludeId " +
           "AND a.status != :cancelledStatus " +
           "AND a.startTime < :endTime AND a.endTime > :startTime")
    boolean hasRoomConflict(
        @Param("roomId") UUID roomId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime,
        @Param("excludeId") UUID excludeId,
        @Param("cancelledStatus") AppointmentStatus cancelledStatus
    );
}
