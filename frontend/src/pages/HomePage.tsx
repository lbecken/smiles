import React from 'react';
import { useAuth } from '@/contexts/AuthContext';

/**
 * Home page component.
 */
const HomePage: React.FC = () => {
  const { isAuthenticated, isLoading, user, login, logout } = useAuth();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
          <div className="text-center mb-6">
            <h1 className="text-3xl font-bold text-gray-900 mb-2">
              Smiles Dental Management
            </h1>
            <p className="text-gray-600">
              Multi-Facility Dental Practice Management System
            </p>
          </div>

          <div className="space-y-4">
            <button
              onClick={login}
              className="w-full bg-primary hover:bg-primary/90 text-white font-medium py-3 px-4 rounded-lg transition-colors"
            >
              Sign In
            </button>

            <div className="text-sm text-gray-500 text-center">
              <p>Phase 0 - Development Environment</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center">
              <h1 className="text-xl font-bold text-gray-900">
                Smiles Dental Management
              </h1>
            </div>
            <div className="flex items-center space-x-4">
              <div className="text-sm text-gray-700">
                <span className="font-medium">{user?.fullName || user?.username}</span>
                <span className="ml-2 text-gray-500">({user?.roles.join(', ')})</span>
              </div>
              <button
                onClick={logout}
                className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-medium py-2 px-4 rounded-lg transition-colors"
              >
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </nav>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="bg-white rounded-lg shadow p-6">
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            Welcome, {user?.firstName || user?.username}!
          </h2>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-3">
                User Information
              </h3>
              <dl className="space-y-2">
                <div>
                  <dt className="text-sm font-medium text-gray-500">Username</dt>
                  <dd className="text-sm text-gray-900">{user?.username}</dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">Email</dt>
                  <dd className="text-sm text-gray-900">{user?.email}</dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">User ID</dt>
                  <dd className="text-sm text-gray-900 font-mono">{user?.userId}</dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">Email Verified</dt>
                  <dd className="text-sm text-gray-900">
                    {user?.emailVerified ? '✅ Yes' : '❌ No'}
                  </dd>
                </div>
              </dl>
            </div>

            <div>
              <h3 className="text-lg font-semibold text-gray-900 mb-3">
                Roles & Permissions
              </h3>
              <div className="space-y-2">
                {user?.roles.map((role) => (
                  <div
                    key={role}
                    className="inline-block bg-blue-100 text-blue-800 text-xs font-medium px-3 py-1 rounded-full mr-2"
                  >
                    {role}
                  </div>
                ))}
              </div>

              {user?.attributes && Object.keys(user.attributes).length > 0 && (
                <div className="mt-4">
                  <h3 className="text-lg font-semibold text-gray-900 mb-3">
                    Additional Attributes
                  </h3>
                  <dl className="space-y-2">
                    {Object.entries(user.attributes).map(([key, value]) => (
                      <div key={key}>
                        <dt className="text-sm font-medium text-gray-500">{key}</dt>
                        <dd className="text-sm text-gray-900">
                          {Array.isArray(value) ? value.join(', ') : String(value)}
                        </dd>
                      </div>
                    ))}
                  </dl>
                </div>
              )}
            </div>
          </div>

          <div className="mt-6 p-4 bg-green-50 border border-green-200 rounded-lg">
            <h4 className="text-sm font-semibold text-green-900 mb-2">
              ✅ Phase 0 Complete - Authentication Working!
            </h4>
            <p className="text-sm text-green-800">
              Your Keycloak authentication is successfully integrated with the Spring Boot backend.
              The JWT token is being validated and user information is being retrieved from the
              /api/auth/me endpoint.
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};

export default HomePage;
