package com.smiles.rooms.mapper;

import com.smiles.rooms.domain.Room;
import com.smiles.rooms.dto.CreateRoomRequest;
import com.smiles.rooms.dto.RoomDto;
import com.smiles.rooms.dto.UpdateRoomRequest;
import org.mapstruct.*;

/**
 * MapStruct mapper for Room entity.
 */
@Mapper(componentModel = "spring")
public interface RoomMapper {

    RoomDto toDto(Room room);

    Room toEntity(CreateRoomRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateRoomRequest request, @MappingTarget Room room);
}
