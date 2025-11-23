import axios, { type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import keycloak from '@/config/keycloak';

/**
 * Base API URL for the backend.
 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081/api';

/**
 * Create an Axios instance with base configuration.
 */
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Request interceptor to add authentication token to all requests.
 */
apiClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    if (keycloak.token) {
      // Update token if it's about to expire (within 5 seconds)
      try {
        await keycloak.updateToken(5);
        config.headers.Authorization = `Bearer ${keycloak.token}`;
      } catch (error) {
        console.error('Failed to refresh token:', error);
        keycloak.login();
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor to handle common errors.
 */
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid, redirect to login
      console.error('Unauthorized - redirecting to login');
      keycloak.login();
    }
    return Promise.reject(error);
  }
);

export default apiClient;
