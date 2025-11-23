# Database Migrations

This directory contains Flyway database migration scripts for the Smiles Dental Management System.

## Naming Convention

Migration files follow the Flyway naming convention:

```
V{version}__{description}.sql
```

Examples:
- `V1__Create_event_publication_table.sql`
- `V2__Create_facilities_table.sql`
- `V3__Create_patients_table.sql`

## Migration Rules

1. **Never modify existing migration files** - Once a migration is applied, it should never be changed
2. **Always create new migrations** - For schema changes, create a new versioned migration
3. **Use sequential versioning** - V1, V2, V3, etc.
4. **Test migrations** - Always test migrations on a development database first
5. **Write reversible migrations** - Consider how to rollback if needed

## Current Migrations

- **V1**: Creates the `event_publication` table required by Spring Modulith for event-driven architecture

## Running Migrations

Migrations are automatically applied when the Spring Boot application starts.

To manually run Flyway commands:

```bash
# Migrate
mvn flyway:migrate

# Info
mvn flyway:info

# Validate
mvn flyway:validate

# Clean (DANGER: drops all objects in schema)
mvn flyway:clean
```

## Baseline

The application is configured with `baseline-on-migrate: true`, which means:
- If the database exists but has no Flyway metadata, it will be baselined
- Migrations will then be applied from that point forward

## Future Migrations

As we build out the application modules, we'll add migrations for:

### Phase 1 - Facilities
- Facilities table
- Facility settings table

### Phase 2 - Staff & Patients
- Staff/employees table
- Patients table
- Insurance information table

### Phase 3 - Scheduling
- Rooms/operatories table
- Appointments table
- Appointment types table

### Phase 4+ - Additional Modules
- EHR tables
- Billing tables
- Inventory tables
- Materials tables
- BOM tables

Each migration will be added as the corresponding module is implemented.
