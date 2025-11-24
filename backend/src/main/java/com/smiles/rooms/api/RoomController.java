package com.smiles.rooms.api;

import com.smiles.rooms.dto.CreateRoomRequest;
import com.smiles.rooms.dto.RoomDto;
import com.smiles.rooms.dto.UpdateRoomRequest;
import com.smiles.rooms.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for room management.
 */
@Slf4j
@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    /**
     * Get all rooms for a facility.
     */
    @GetMapping
    public ResponseEntity<List<RoomDto>> getRoomsByFacility(@RequestParam UUID facilityId) {
        log.debug("GET /rooms?facilityId={} - Get rooms by facility", facilityId);
        List<RoomDto> rooms = roomService.getRoomsByFacility(facilityId);
        return ResponseEntity.ok(rooms);
    }

    /**
     * Get room by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable UUID id) {
        log.debug("GET /rooms/{} - Get room by ID", id);
        RoomDto room = roomService.getRoomById(id);
        return ResponseEntity.ok(room);
    }

    /**
     * Create a new room (admin and receptionist).
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('admin', 'receptionist')")
    public ResponseEntity<RoomDto> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        log.debug("POST /rooms - Create room: {}", request.getName());
        RoomDto created = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update an existing room (admin and receptionist).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('admin', 'receptionist')")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoomRequest request) {
        log.debug("PUT /rooms/{} - Update room", id);
        RoomDto updated = roomService.updateRoom(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete a room (admin only).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID id) {
        log.debug("DELETE /rooms/{} - Delete room", id);
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
