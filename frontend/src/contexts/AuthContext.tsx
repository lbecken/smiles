import React, { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import keycloak from '@/config/keycloak';
import { authService } from '@/services/auth.service';
import { UserInfo, AuthState } from '@/types/auth';

/**
 * Authentication context type.
 */
interface AuthContextType extends AuthState {
  login: () => void;
  logout: () => void;
  hasRole: (role: string) => boolean;
  hasAnyRole: (roles: string[]) => boolean;
  refreshUserInfo: () => Promise<void>;
}

/**
 * Create the authentication context.
 */
const AuthContext = createContext<AuthContextType | undefined>(undefined);

/**
 * Authentication provider props.
 */
interface AuthProviderProps {
  children: ReactNode;
}

/**
 * Authentication provider component.
 */
export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [authState, setAuthState] = useState<AuthState>({
    isAuthenticated: false,
    isLoading: true,
    user: null,
    token: null,
  });

  /**
   * Fetch user information from the backend.
   */
  const fetchUserInfo = async () => {
    try {
      const userInfo = await authService.getCurrentUser();
      setAuthState((prev) => ({
        ...prev,
        user: userInfo,
        isAuthenticated: true,
        isLoading: false,
        token: keycloak.token || null,
      }));
    } catch (error) {
      console.error('Failed to fetch user info:', error);
      setAuthState((prev) => ({
        ...prev,
        isAuthenticated: false,
        isLoading: false,
        user: null,
        token: null,
      }));
    }
  };

  /**
   * Initialize Keycloak and set up authentication.
   */
  useEffect(() => {
    const initKeycloak = async () => {
      try {
        const authenticated = await keycloak.init({
          onLoad: 'check-sso',
          silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
          pkceMethod: 'S256',
        });

        if (authenticated) {
          await fetchUserInfo();
        } else {
          setAuthState({
            isAuthenticated: false,
            isLoading: false,
            user: null,
            token: null,
          });
        }

        // Set up token refresh
        keycloak.onTokenExpired = () => {
          keycloak
            .updateToken(30)
            .then((refreshed) => {
              if (refreshed) {
                console.log('Token refreshed');
              }
            })
            .catch(() => {
              console.error('Failed to refresh token');
              logout();
            });
        };
      } catch (error) {
        console.error('Failed to initialize Keycloak:', error);
        setAuthState({
          isAuthenticated: false,
          isLoading: false,
          user: null,
          token: null,
        });
      }
    };

    initKeycloak();
  }, []);

  /**
   * Login function.
   */
  const login = () => {
    keycloak.login();
  };

  /**
   * Logout function.
   */
  const logout = () => {
    keycloak.logout();
  };

  /**
   * Check if the user has a specific role.
   */
  const hasRole = (role: string): boolean => {
    return authState.user?.roles?.includes(role) ?? false;
  };

  /**
   * Check if the user has any of the specified roles.
   */
  const hasAnyRole = (roles: string[]): boolean => {
    return roles.some((role) => hasRole(role));
  };

  /**
   * Refresh user information from the backend.
   */
  const refreshUserInfo = async () => {
    await fetchUserInfo();
  };

  const value: AuthContextType = {
    ...authState,
    login,
    logout,
    hasRole,
    hasAnyRole,
    refreshUserInfo,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

/**
 * Hook to use the authentication context.
 */
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
