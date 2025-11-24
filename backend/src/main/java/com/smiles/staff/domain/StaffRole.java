package com.smiles.staff.domain;

/**
 * Enum representing staff roles.
 */
public enum StaffRole {
    /**
     * Dentist - can perform dental procedures.
     */
    dentist("dentist"),

    /**
     * Dental assistant - assists dentists.
     */
    assistant("assistant"),

    /**
     * Receptionist - handles front desk operations.
     */
    receptionist("receptionist"),

    /**
     * Admin - administrative staff with elevated permissions.
     */
    admin("admin");

    private final String value;

    StaffRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StaffRole fromValue(String value) {
        for (StaffRole role : StaffRole.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown staff role: " + value);
    }
}
