package com.smiles.rooms.repository;

import com.smiles.rooms.domain.Room;
import com.smiles.rooms.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Room entity.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    /**
     * Find all rooms for a facility.
     */
    List<Room> findByFacilityId(UUID facilityId);

    /**
     * Find all rooms for a facility by type.
     */
    List<Room> findByFacilityIdAndType(UUID facilityId, RoomType type);

    /**
     * Find room by facility and name.
     */
    Optional<Room> findByFacilityIdAndName(UUID facilityId, String name);

    /**
     * Check if room exists by facility and name.
     */
    boolean existsByFacilityIdAndName(UUID facilityId, String name);
}
