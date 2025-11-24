-- Phase 1: Core tables for Facility, Room, Staff, and Patient

-- =====================================================
-- FACILITY TABLE
-- =====================================================
CREATE TABLE facility (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_facility_name UNIQUE (name)
);

CREATE INDEX idx_facility_city ON facility(city);

COMMENT ON TABLE facility IS 'Dental facilities/clinics in the multi-facility system';
COMMENT ON COLUMN facility.name IS 'Unique facility name';
COMMENT ON COLUMN facility.city IS 'City where the facility is located';
COMMENT ON COLUMN facility.address IS 'Full street address of the facility';

-- =====================================================
-- ROOM TABLE
-- =====================================================
CREATE TABLE room (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facility_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE CASCADE,
    CONSTRAINT uk_room_facility_name UNIQUE (facility_id, name),
    CONSTRAINT chk_room_type CHECK (type IN ('chair', 'surgery_room'))
);

CREATE INDEX idx_room_facility ON room(facility_id);
CREATE INDEX idx_room_type ON room(type);

COMMENT ON TABLE room IS 'Treatment rooms and operatories within facilities';
COMMENT ON COLUMN room.name IS 'Room identifier (e.g., "Room 1", "Chair A")';
COMMENT ON COLUMN room.type IS 'Type of room: chair or surgery_room';
COMMENT ON COLUMN room.facility_id IS 'Reference to the parent facility';

-- =====================================================
-- STAFF TABLE
-- =====================================================
CREATE TABLE staff (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facility_id UUID NOT NULL,
    keycloak_user_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE CASCADE,
    CONSTRAINT uk_staff_email UNIQUE (email),
    CONSTRAINT uk_staff_keycloak_user UNIQUE (keycloak_user_id),
    CONSTRAINT chk_staff_role CHECK (role IN ('dentist', 'assistant', 'receptionist', 'admin'))
);

CREATE INDEX idx_staff_facility ON staff(facility_id);
CREATE INDEX idx_staff_role ON staff(role);
CREATE INDEX idx_staff_keycloak_user ON staff(keycloak_user_id);
CREATE INDEX idx_staff_email ON staff(email);

COMMENT ON TABLE staff IS 'Staff members working at facilities';
COMMENT ON COLUMN staff.keycloak_user_id IS 'Keycloak user ID for authentication';
COMMENT ON COLUMN staff.name IS 'Full name of the staff member';
COMMENT ON COLUMN staff.email IS 'Email address (unique across all facilities)';
COMMENT ON COLUMN staff.role IS 'Role: dentist, assistant, receptionist, or admin';
COMMENT ON COLUMN staff.facility_id IS 'Primary facility assignment';
COMMENT ON COLUMN staff.active IS 'Whether the staff member is currently active';

-- =====================================================
-- PATIENT TABLE
-- =====================================================
CREATE TABLE patient (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    facility_id UUID NOT NULL,
    keycloak_user_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    address VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_patient_facility FOREIGN KEY (facility_id) REFERENCES facility(id) ON DELETE CASCADE,
    CONSTRAINT uk_patient_keycloak_user UNIQUE (keycloak_user_id)
);

CREATE INDEX idx_patient_facility ON patient(facility_id);
CREATE INDEX idx_patient_name ON patient(name);
CREATE INDEX idx_patient_keycloak_user ON patient(keycloak_user_id);
CREATE INDEX idx_patient_email ON patient(email);
CREATE INDEX idx_patient_birth_date ON patient(birth_date);

COMMENT ON TABLE patient IS 'Patients registered at facilities';
COMMENT ON COLUMN patient.keycloak_user_id IS 'Optional Keycloak user ID if patient has portal access';
COMMENT ON COLUMN patient.name IS 'Full name of the patient';
COMMENT ON COLUMN patient.birth_date IS 'Date of birth';
COMMENT ON COLUMN patient.email IS 'Email address for contact';
COMMENT ON COLUMN patient.phone IS 'Phone number for contact';
COMMENT ON COLUMN patient.address IS 'Home address';
COMMENT ON COLUMN patient.facility_id IS 'Primary facility where patient is registered';
COMMENT ON COLUMN patient.active IS 'Whether the patient record is currently active';

-- =====================================================
-- UPDATED_AT TRIGGER FUNCTION
-- =====================================================
-- Function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply trigger to all tables
CREATE TRIGGER update_facility_updated_at BEFORE UPDATE ON facility
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_room_updated_at BEFORE UPDATE ON room
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_staff_updated_at BEFORE UPDATE ON staff
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_patient_updated_at BEFORE UPDATE ON patient
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
