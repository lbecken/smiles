package com.smiles.appointments.domain;

/**
 * Enum representing appointment status.
 */
public enum AppointmentStatus {
    /**
     * Appointment is scheduled and confirmed.
     */
    SCHEDULED("scheduled"),

    /**
     * Appointment is currently in progress.
     */
    ONGOING("ongoing"),

    /**
     * Appointment has been completed.
     */
    COMPLETED("completed"),

    /**
     * Appointment has been cancelled.
     */
    CANCELLED("cancelled");

    private final String value;

    AppointmentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AppointmentStatus fromValue(String value) {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown appointment status: " + value);
    }
}
