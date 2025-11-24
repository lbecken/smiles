package com.smiles.appointments.mapper;

import com.smiles.appointments.domain.Appointment;
import com.smiles.appointments.domain.AppointmentStatus;
import com.smiles.appointments.dto.AppointmentDto;
import com.smiles.appointments.dto.CreateAppointmentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Appointment entity.
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    /**
     * Convert Appointment entity to DTO.
     */
    AppointmentDto toDto(Appointment appointment);

    /**
     * Convert CreateAppointmentRequest to Appointment entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(CreateAppointmentRequest request);
}
