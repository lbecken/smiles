package com.smiles.rooms.service;

import com.smiles.common.security.SecurityUtils;
import com.smiles.rooms.domain.Room;
import com.smiles.rooms.domain.RoomType;
import com.smiles.rooms.dto.CreateRoomRequest;
import com.smiles.rooms.dto.RoomDto;
import com.smiles.rooms.dto.UpdateRoomRequest;
import com.smiles.rooms.mapper.RoomMapper;
import com.smiles.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing rooms.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final SecurityUtils securityUtils;

    /**
     * Get all rooms for a facility.
     */
    public List<RoomDto> getRoomsByFacility(UUID facilityId) {
        log.debug("Getting rooms for facility: {}", facilityId);

        // Check access permission
        securityUtils.checkFacilityAccess(facilityId);

        return roomRepository.findByFacilityId(facilityId).stream()
                .map(roomMapper::toDto)
                .toList();
    }

    /**
     * Get room by ID.
     */
    public RoomDto getRoomById(UUID id) {
        log.debug("Getting room by id: {}", id);
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(room.getFacilityId());

        return roomMapper.toDto(room);
    }

    /**
     * Create a new room.
     */
    @Transactional
    public RoomDto createRoom(CreateRoomRequest request) {
        log.debug("Creating room: {} for facility: {}", request.getName(), request.getFacilityId());

        // Check access permission
        securityUtils.checkFacilityAccess(request.getFacilityId());

        if (roomRepository.existsByFacilityIdAndName(request.getFacilityId(), request.getName())) {
            throw new IllegalArgumentException(
                    "Room already exists with name: " + request.getName() + " in facility: " + request.getFacilityId()
            );
        }

        Room room = roomMapper.toEntity(request);
        Room savedRoom = roomRepository.save(room);
        log.info("Created room: {} with id: {}", savedRoom.getName(), savedRoom.getId());

        return roomMapper.toDto(savedRoom);
    }

    /**
     * Update an existing room.
     */
    @Transactional
    public RoomDto updateRoom(UUID id, UpdateRoomRequest request) {
        log.debug("Updating room with id: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(room.getFacilityId());

        // Check if name is being changed and if it conflicts
        if (request.getName() != null && !request.getName().equals(room.getName())) {
            if (roomRepository.existsByFacilityIdAndName(room.getFacilityId(), request.getName())) {
                throw new IllegalArgumentException(
                        "Room already exists with name: " + request.getName() + " in facility: " + room.getFacilityId()
                );
            }
        }

        roomMapper.updateEntityFromDto(request, room);
        Room updatedRoom = roomRepository.save(room);
        log.info("Updated room with id: {}", id);

        return roomMapper.toDto(updatedRoom);
    }

    /**
     * Delete a room.
     */
    @Transactional
    public void deleteRoom(UUID id) {
        log.debug("Deleting room with id: {}", id);

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with id: " + id));

        // Check access permission
        securityUtils.checkFacilityAccess(room.getFacilityId());

        roomRepository.deleteById(id);
        log.info("Deleted room with id: {}", id);
    }
}
