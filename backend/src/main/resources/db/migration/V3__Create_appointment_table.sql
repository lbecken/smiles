-- Phase 2: Appointment table for basic scheduling

-- =====================================================
-- APPOINTMENT TABLE
-- =====================================================
CREATE TABLE appointment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL,
    dentist_id UUID NOT NULL,
    room_id UUID NOT NULL,
    facility_id UUID NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointment_patient FOREIGN KEY (patient_id) REFERENCES patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_dentist FOREIGN KEY (dentist_id) REFERENCES staff(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_room FOREIGN KEY (room_id) REFERENCES room(id) ON DELETE CASCADE,
    CONSTRAINT fk_appointment_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE CASCADE,
    CONSTRAINT chk_appointment_status CHECK (status IN ('scheduled', 'ongoing', 'completed', 'cancelled')),
    CONSTRAINT chk_appointment_times CHECK (start_time < end_time)
);

CREATE INDEX idx_appointment_patient ON appointment(patient_id);
CREATE INDEX idx_appointment_dentist ON appointment(dentist_id);
CREATE INDEX idx_appointment_room ON appointment(room_id);
CREATE INDEX idx_appointment_facility ON appointment(facility_id);
CREATE INDEX idx_appointment_start_time ON appointment(start_time);
CREATE INDEX idx_appointment_end_time ON appointment(end_time);
CREATE INDEX idx_appointment_status ON appointment(status);

-- Composite index for conflict detection queries
CREATE INDEX idx_appointment_dentist_time ON appointment(dentist_id, start_time, end_time) WHERE status != 'cancelled';
CREATE INDEX idx_appointment_room_time ON appointment(room_id, start_time, end_time) WHERE status != 'cancelled';

COMMENT ON TABLE appointment IS 'Appointments scheduled at facilities';
COMMENT ON COLUMN appointment.patient_id IS 'Patient for this appointment';
COMMENT ON COLUMN appointment.dentist_id IS 'Dentist (staff) assigned to this appointment';
COMMENT ON COLUMN appointment.room_id IS 'Room where appointment takes place';
COMMENT ON COLUMN appointment.facility_id IS 'Facility where appointment is scheduled';
COMMENT ON COLUMN appointment.start_time IS 'Scheduled start time';
COMMENT ON COLUMN appointment.end_time IS 'Scheduled end time';
COMMENT ON COLUMN appointment.status IS 'Status: scheduled, ongoing, completed, or cancelled';

-- Apply updated_at trigger
CREATE TRIGGER update_appointment_updated_at BEFORE UPDATE ON appointment
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
