-- Create Keycloak database
CREATE DATABASE keycloak_db;

-- Grant privileges to the smiles_user on keycloak_db
GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO smiles_user;
