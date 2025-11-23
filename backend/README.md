# Smiles Dental Management - Backend

Backend service for the Smiles Dental Multi-Facility Management System.

## Tech Stack

- **Java**: 21
- **Spring Boot**: 3.3.0
- **Spring Modulith**: 1.2.0
- **Database**: PostgreSQL 16
- **Security**: OAuth2 Resource Server (Keycloak)
- **Build Tool**: Maven

## Architecture

This application uses **Spring Modulith** to organize code into clearly defined modules:

### Core Modules

- **common**: Shared utilities, security, and base classes
- **auth**: Authentication and authorization
- **facilities**: Multi-facility management

### Domain Modules

- **staff**: Employee and dentist management
- **patients**: Patient records and demographics
- **rooms**: Treatment rooms/operatories
- **appointments**: Scheduling and calendar
- **ehr**: Electronic Health Records
- **materials**: Dental materials catalog
- **inventory**: Facility-specific inventory
- **bom**: Bill of Materials for procedures
- **billing**: Invoicing and payments
- **realtime**: WebSocket-based real-time updates

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- Docker and Docker Compose (for infrastructure)

### Running the Application

1. **Start Infrastructure**:
   ```bash
   # From project root
   docker-compose up -d
   ```

2. **Build the Application**:
   ```bash
   cd backend
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8081/api`

### API Endpoints

#### Authentication
- `GET /api/auth/me` - Get current user information
- `GET /api/auth/health` - Authentication health check

#### Health & Monitoring
- `GET /api/actuator/health` - Application health
- `GET /api/actuator/info` - Application info

## Configuration

Main configuration in `src/main/resources/application.yml`:

- Database connection
- Keycloak integration
- CORS settings
- Logging levels

## Testing the API

### Using cURL

```bash
# Get access token
TOKEN=$(curl -X POST 'http://localhost:8080/realms/smiles/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'client_id=smiles-frontend' \
  -d 'username=admin' \
  -d 'password=admin123' \
  -d 'grant_type=password' | jq -r '.access_token')

# Call /api/me endpoint
curl -X GET 'http://localhost:8081/api/auth/me' \
  -H "Authorization: Bearer $TOKEN"
```

## Development

### Module Structure

Each module follows this internal structure:
```
module/
├── api/          # REST controllers
├── dto/          # Data Transfer Objects
├── domain/       # Domain entities
├── repository/   # Data repositories
├── service/      # Business logic
└── package-info.java
```

### Adding a New Module

1. Create module package under `com.smiles`
2. Add `package-info.java` with module documentation
3. Follow module boundaries (no circular dependencies)
4. Use Spring Modulith events for cross-module communication

## Security

- All endpoints require JWT authentication (except health checks)
- Roles: ADMIN, DENTIST, STAFF, PATIENT
- CORS enabled for frontend origins
- Stateless session management

## Database Migrations

Using Flyway for database migrations:
- Scripts location: `src/main/resources/db/migration`
- Naming: `V{version}__{description}.sql`
- Applied automatically on startup
