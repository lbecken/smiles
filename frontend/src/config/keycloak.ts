import Keycloak from 'keycloak-js';

/**
 * Keycloak configuration for the Smiles Dental application.
 */
const keycloakConfig = {
  url: 'http://localhost:8080',
  realm: 'smiles',
  clientId: 'smiles-frontend',
};

/**
 * Create and export a Keycloak instance.
 */
const keycloak = new Keycloak(keycloakConfig);

export default keycloak;
