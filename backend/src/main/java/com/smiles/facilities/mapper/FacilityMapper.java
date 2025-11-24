package com.smiles.facilities.mapper;

import com.smiles.facilities.domain.Facility;
import com.smiles.facilities.dto.CreateFacilityRequest;
import com.smiles.facilities.dto.FacilityDto;
import com.smiles.facilities.dto.UpdateFacilityRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Facility entity.
 */
@Mapper(componentModel = "spring")
public interface FacilityMapper {

    FacilityDto toDto(Facility facility);

    Facility toEntity(CreateFacilityRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateFacilityRequest request, @MappingTarget Facility facility);
}
