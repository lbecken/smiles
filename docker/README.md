# Docker Infrastructure Setup

This directory contains the Docker configuration for the Smiles Dental Management System.

## Services

### PostgreSQL (Port 5432)
- **Database**: smiles_db
- **Username**: smiles_user
- **Password**: smiles_password
- Contains both application data and Keycloak data (separate databases)

### pgAdmin (Port 5050)
- **URL**: http://localhost:5050
- **Email**: admin@smiles.local
- **Password**: admin123

To connect to PostgreSQL from pgAdmin:
- Host: postgres
- Port: 5432
- Database: smiles_db
- Username: smiles_user
- Password: smiles_password

### Keycloak (Port 8080)
- **URL**: http://localhost:8080
- **Admin Console**: http://localhost:8080/admin
- **Admin Username**: admin
- **Admin Password**: admin123
- **Realm**: smiles

## Quick Start

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove volumes (clean start)
docker-compose down -v
```

## Test Users

Once Keycloak is running, the following test users are available:

| Username      | Password    | Role     | Description |
|---------------|-------------|----------|-------------|
| admin         | admin123    | admin    | System administrator |
| dr.smith      | dentist123  | dentist  | Dentist user |
| jane.doe      | staff123    | staff    | Staff user |
| patient.test  | patient123  | patient  | Patient user |

## Keycloak Realm Configuration

The realm is automatically imported from `./keycloak/realms/smiles-realm.json` on first startup.

**Clients**:
- `smiles-frontend`: Public client for React app
- `smiles-backend`: Bearer-only client for Spring Boot API

**Roles**:
- admin: Full system access
- dentist: Access to patient records and appointments
- staff: Access to scheduling and basic patient info
- patient: Access to own records and appointments

## Health Checks

- PostgreSQL: `docker-compose exec postgres pg_isready -U smiles_user`
- Keycloak: http://localhost:8080/health/ready

## Troubleshooting

### Keycloak won't start
- Ensure PostgreSQL is healthy first
- Check logs: `docker-compose logs keycloak`
- Wait for the startup (can take 60-90 seconds on first run)

### Database connection issues
- Verify PostgreSQL is running: `docker-compose ps postgres`
- Check network: `docker network ls | grep smiles`
