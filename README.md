# Smiles Dental Management System

A comprehensive multi-facility dental practice management application built with modern technologies.

## Overview

Smiles is a full-stack dental management system designed to handle multiple dental facilities with features including:
- Patient management and EHR
- Multi-facility scheduling and appointments
- Staff and dentist management
- Inventory tracking (facility-specific)
- Billing and invoicing
- Real-time updates via WebSockets

## Tech Stack

### Frontend
- **Framework**: React 18 + TypeScript
- **Build Tool**: Vite
- **UI Library**: ShadCN UI (Tailwind CSS)
- **State Management**: React Query (TanStack Query)
- **Authentication**: Keycloak JS Adapter

### Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.3
- **Architecture**: Spring Modulith (Modular Monolith)
- **Database**: PostgreSQL 16
- **Security**: OAuth2 Resource Server (Keycloak)
- **Build Tool**: Maven

### Infrastructure
- **Authentication**: Keycloak 23
- **Database**: PostgreSQL 16
- **Database Admin**: pgAdmin
- **Containerization**: Docker & Docker Compose

## Quick Start

### Prerequisites

- **Docker & Docker Compose**: For infrastructure services
- **Java 21**: For backend development
- **Maven 3.9+**: For building backend
- **Node.js 18+**: For frontend development
- **npm**: For frontend package management

### 1. Start Infrastructure Services

```bash
# Start Keycloak, PostgreSQL, and pgAdmin
docker-compose up -d

# Wait for services to be healthy (check logs)
docker-compose logs -f keycloak

# Services will be available at:
# - Keycloak: http://localhost:8080
# - PostgreSQL: localhost:5432
# - pgAdmin: http://localhost:5050
```

### 2. Start Backend

```bash
cd backend

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run

# Backend API will be available at:
# http://localhost:8081/api
```

### 3. Start Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend will be available at:
# http://localhost:5173
```

## Testing Phase 0 - Authentication

Phase 0 is complete! Test the authentication system:

1. Navigate to `http://localhost:5173`
2. Click "Sign In"
3. Use one of these test accounts:

| Username      | Password    | Role     |
|---------------|-------------|----------|
| admin         | admin123    | ADMIN    |
| dr.smith      | dentist123  | DENTIST  |
| jane.doe      | staff123    | STAFF    |
| patient.test  | patient123  | PATIENT  |

4. After login, you should see:
   - User information from JWT token
   - Roles and permissions
   - Data from the `/api/auth/me` endpoint

## Project Structure

```
smiles/
├── docker/                    # Docker configuration
│   ├── keycloak/             # Keycloak realm configuration
│   └── init-scripts/         # Database initialization scripts
├── backend/                   # Spring Boot backend
│   ├── src/main/java/com/smiles/
│   │   ├── common/           # Shared utilities and security
│   │   ├── auth/             # Authentication module
│   │   ├── facilities/       # Facilities module
│   │   ├── staff/            # Staff management module
│   │   ├── patients/         # Patient management module
│   │   ├── rooms/            # Treatment rooms module
│   │   ├── appointments/     # Scheduling module
│   │   ├── ehr/              # Electronic Health Records module
│   │   ├── materials/        # Materials catalog module
│   │   ├── inventory/        # Inventory management module
│   │   ├── bom/              # Bill of Materials module
│   │   ├── billing/          # Billing and invoicing module
│   │   └── realtime/         # WebSocket real-time updates module
│   └── pom.xml
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── contexts/         # React contexts
│   │   ├── pages/            # Page components
│   │   ├── services/         # API services
│   │   └── types/            # TypeScript types
│   └── package.json
├── docker-compose.yml         # Infrastructure services
└── README.md                  # This file
```

## Architecture

### Spring Modulith Modules

The backend uses Spring Modulith to organize code into clearly defined modules:

- **common**: Shared utilities, security, and base classes
- **auth**: Authentication and authorization
- **facilities**: Multi-facility management
- **staff**: Employee and dentist management
- **patients**: Patient records and demographics
- **rooms**: Treatment rooms/operatories
- **appointments**: Scheduling and calendar
- **ehr**: Electronic Health Records
- **materials**: Dental materials catalog
- **inventory**: Facility-specific inventory tracking
- **bom**: Bill of Materials for procedures
- **billing**: Invoicing and payments
- **realtime**: WebSocket-based real-time updates

Each module has clear boundaries and communicates with others through:
- Direct API calls (within same bounded context)
- Spring Modulith events (for cross-module communication)

## Development Phases

### ✅ Phase 0 - Development Environment Setup (Complete)

- [x] Docker Compose with Keycloak, PostgreSQL, pgAdmin
- [x] Spring Boot backend with Spring Modulith
- [x] Module structure for all domains
- [x] Keycloak integration with JWT validation
- [x] React frontend with Vite
- [x] ShadCN UI configuration
- [x] React Query setup
- [x] Authentication flow with Keycloak JS
- [x] `/api/auth/me` endpoint
- [x] Test users for all roles

### 🚧 Next Phases (Planned)

See the incremental development plan for upcoming phases including:
- Facility management
- Staff and patient registration
- Appointment scheduling
- EHR and treatment plans
- Billing and invoicing
- Real-time updates

## API Documentation

### Authentication Endpoints

- `GET /api/auth/me` - Get current user information
- `GET /api/auth/health` - Authentication health check

### Health & Monitoring

- `GET /api/actuator/health` - Application health
- `GET /api/actuator/info` - Application info

## Configuration

### Keycloak

- **Admin Console**: http://localhost:8080/admin
- **Admin Username**: admin
- **Admin Password**: admin123
- **Realm**: smiles
- **Frontend Client**: smiles-frontend (public)
- **Backend Client**: smiles-backend (bearer-only)

### Database

- **Host**: localhost:5432
- **Database**: smiles_db
- **Username**: smiles_user
- **Password**: smiles_password

### pgAdmin

- **URL**: http://localhost:5050
- **Email**: admin@smiles.local
- **Password**: admin123

## Contributing

This project follows a modular architecture:
1. Each module is self-contained
2. No circular dependencies between modules
3. Use Spring Modulith events for cross-module communication
4. Keep DTOs and entities separate

## Troubleshooting

### Keycloak won't start
```bash
# Check if PostgreSQL is healthy first
docker-compose ps

# View Keycloak logs
docker-compose logs keycloak

# Restart Keycloak
docker-compose restart keycloak
```

### Backend can't connect to database
```bash
# Verify PostgreSQL is running
docker-compose ps postgres

# Check backend logs
cd backend && mvn spring-boot:run
```

### Frontend authentication fails
1. Verify Keycloak is running at http://localhost:8080
2. Check backend is running at http://localhost:8081
3. Verify CORS settings in backend `application.yml`
4. Check browser console for errors

## License

Proprietary - All rights reserved

## Support

For issues or questions, please contact the development team.
