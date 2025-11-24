package com.smiles.patients.mapper;

import com.smiles.patients.domain.Patient;
import com.smiles.patients.dto.CreatePatientRequest;
import com.smiles.patients.dto.PatientDto;
import com.smiles.patients.dto.UpdatePatientRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Patient entity.
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDto toDto(Patient patient);

    Patient toEntity(CreatePatientRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdatePatientRequest request, @MappingTarget Patient patient);
}
