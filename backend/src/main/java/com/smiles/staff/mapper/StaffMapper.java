package com.smiles.staff.mapper;

import com.smiles.staff.domain.Staff;
import com.smiles.staff.dto.CreateStaffRequest;
import com.smiles.staff.dto.StaffDto;
import com.smiles.staff.dto.UpdateStaffRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Staff entity.
 */
@Mapper(componentModel = "spring")
public interface StaffMapper {

    StaffDto toDto(Staff staff);

    Staff toEntity(CreateStaffRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateStaffRequest request, @MappingTarget Staff staff);
}
