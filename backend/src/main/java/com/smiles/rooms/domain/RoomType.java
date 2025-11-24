package com.smiles.rooms.domain;

/**
 * Enum representing the type of room.
 */
public enum RoomType {
    /**
     * Dental chair for routine treatments.
     */
    CHAIR("chair"),

    /**
     * Surgery room for surgical procedures.
     */
    SURGERY_ROOM("surgery_room");

    private final String value;

    RoomType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static RoomType fromValue(String value) {
        for (RoomType type : RoomType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown room type: " + value);
    }
}
