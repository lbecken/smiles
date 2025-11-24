# Phase 1 Implementation - Core Entities

## Overview
This document describes the implementation of Phase 1 for the Smiles Dental Management System, which establishes the foundation with core entities: Facility, Room, Staff, and Patient.

## Implemented Components

### 1. Database Schema

**Migration File:** `backend/src/main/resources/db/migration/V2__Create_core_tables.sql`

Created four core tables:
- **facility**: Dental clinics/facilities
  - id (UUID), name, city, address
  - Unique constraint on name

- **room**: Treatment rooms and operatories
  - id (UUID), facility_id, name, type ('chair' or 'surgery_room')
  - Unique constraint on (facility_id, name)

- **staff**: Staff members at facilities
  - id (UUID), facility_id, keycloak_user_id, name, email, role, active
  - Roles: dentist, assistant, receptionist, admin
  - Unique constraints on email and keycloak_user_id

- **patient**: Patients registered at facilities
  - id (UUID), facility_id, keycloak_user_id, name, birth_date, email, phone, address, active
  - Unique constraint on keycloak_user_id

All tables include:
- Automatic timestamp management (created_at, updated_at)
- Foreign key constraints with cascading deletes
- Appropriate indexes for performance

### 2. Backend Implementation (Spring Boot)

#### Domain Layer
- **Entities**: JPA entities for Facility, Room, Staff, Patient
- **Enums**: RoomType, StaffRole

#### Repository Layer
- Spring Data JPA repositories with custom query methods
- Methods for filtering by facility, role, type, etc.

#### Service Layer
Services with business logic and access control:
- **FacilityService**: CRUD operations for facilities
- **RoomService**: Room management with facility-level access control
- **StaffService**: Staff management with Keycloak user linking
- **PatientService**: Patient management with Keycloak user linking

#### API Layer (REST Controllers)
RESTful endpoints for all entities:
- **FacilityController** (`/api/facilities`)
  - GET / - List all facilities (admin only)
  - GET /{id} - Get facility by ID
  - POST / - Create facility (admin only)
  - PUT /{id} - Update facility (admin only)
  - DELETE /{id} - Delete facility (admin only)

- **RoomController** (`/api/rooms`)
  - GET ?facilityId={id} - List rooms by facility
  - GET /{id} - Get room by ID
  - POST / - Create room (admin, receptionist)
  - PUT /{id} - Update room (admin, receptionist)
  - DELETE /{id} - Delete room (admin only)

- **StaffController** (`/api/staff`)
  - GET ?facilityId={id} - List staff by facility
  - GET /{id} - Get staff by ID
  - GET /by-keycloak/{id} - Get staff by Keycloak user ID
  - POST / - Create staff (admin only)
  - PUT /{id} - Update staff (admin only)
  - DELETE /{id} - Delete staff (admin only)
  - POST /{id}/link-keycloak/{userId} - Link Keycloak user (admin only)

- **PatientController** (`/api/patients`)
  - GET ?facilityId={id} - List patients by facility
  - GET /{id} - Get patient by ID
  - GET /by-keycloak/{id} - Get patient by Keycloak user ID
  - POST / - Create patient (admin, receptionist)
  - PUT /{id} - Update patient (admin, receptionist)
  - DELETE /{id} - Delete patient (admin only)
  - POST /{id}/link-keycloak/{userId} - Link Keycloak user (admin, receptionist)

#### Security Implementation
- **SecurityUtils**: Enhanced security utility component for access control
- **FacilityAccessChecker**: Validates user access to facilities
- **Role-based Access Control**:
  - Admins: Full access to all facilities and operations
  - Receptionists: Access only to their assigned facility
  - Access control enforced at service layer using `SecurityUtils.checkFacilityAccess()`

### 3. Frontend Implementation (React + TypeScript)

#### TypeScript Types
- `types/facility.ts` - Facility interface and DTOs
- `types/room.ts` - Room interface, DTOs, and RoomType enum
- `types/staff.ts` - Staff interface, DTOs, and StaffRole enum
- `types/patient.ts` - Patient interface and DTOs

#### API Services
Type-safe API service modules using Axios:
- `services/facility.service.ts`
- `services/room.service.ts`
- `services/staff.service.ts`
- `services/patient.service.ts`

#### React Components (Pages)
- **FacilityListPage**: Lists all facilities (admin only)
- **RoomListPage**: Lists rooms per selected facility
- **StaffListPage**: Lists staff per selected facility
- **PatientListPage**: Lists patients per selected facility

All list pages include:
- React Query for data fetching and caching
- Loading and error states
- Facility selection dropdown (for room, staff, patient pages)
- Responsive card-based layout using Tailwind CSS
- Empty state messages

### 4. Integration Tests

Comprehensive test coverage for all controllers:
- **FacilityControllerTest**: Tests for facility CRUD operations and admin-only access
- **RoomControllerTest**: Tests for room operations with facility association
- **StaffControllerTest**: Tests for staff management and role-based access
- **PatientControllerTest**: Tests for patient operations

Test scenarios include:
- ✅ Create 2-3 facilities
- ✅ Assign staff to correct facility
- ✅ Create rooms per facility
- ✅ Ensure receptionist cannot access unauthorized operations
- ✅ Test role-based access control

## Security Features

1. **OAuth2/JWT Integration**: All endpoints secured with Keycloak authentication
2. **Role-Based Access Control**:
   - `@PreAuthorize` annotations on controller methods
   - Service-level access checks for facility isolation
3. **Facility Isolation**: Users can only access data from their assigned facility (except admins)
4. **Keycloak User Linking**: Staff and patients can be linked to Keycloak users for authentication

## Data Validation

- Jakarta Bean Validation on all request DTOs
- Email validation for staff and patient records
- Enum validation for roles and room types
- Unique constraints on critical fields (emails, names)

## API Documentation

Base URL: `http://localhost:8081/api`

All endpoints require authentication via Bearer token in the Authorization header.

### Common Response Codes
- `200 OK` - Successful GET/PUT
- `201 Created` - Successful POST
- `204 No Content` - Successful DELETE
- `400 Bad Request` - Validation error
- `401 Unauthorized` - Missing/invalid authentication
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found

## Next Steps (Phase 2+)

The foundation is now in place for:
- Appointments scheduling
- Electronic Health Records (EHR)
- Materials catalog and inventory tracking
- Bill of Materials (BOM) for procedures
- Billing and invoicing
- Real-time updates via WebSocket

## Running the Application

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Database
PostgreSQL and Keycloak are configured in `docker-compose.yml`:
```bash
docker-compose up -d
```

## Testing

Run backend tests:
```bash
cd backend
mvn test
```

## Database Migrations

Flyway automatically applies migrations on startup. Current migrations:
- V1: Event publication table (Spring Modulith)
- V2: Core tables (Facility, Room, Staff, Patient)
