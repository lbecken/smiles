import React, { useState } from "react";
import { useAuth } from "@/contexts/AuthContext";
import AppointmentsPage from "./AppointmentsPage";
import FacilityListPage from "./FacilityListPage";
import StaffListPage from "./StaffListPage";
import RoomListPage from "./RoomListPage";
import PatientListPage from "./PatientListPage";

type Page = "home" | "appointments" | "facilities" | "staff" | "rooms" | "patients";

/**
 * Home page component.
 */
const HomePage: React.FC = () => {
  const { isAuthenticated, isLoading, user, login, logout } = useAuth();
  const [currentPage, setCurrentPage] = useState<Page>("home");
  const useStyles = true;

  if (isLoading && !useStyles) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  if (isLoading && useStyles) {
    return (
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
        }}
      >
        <div style={{ textAlign: "center" }}>
          <div
            style={{
              border: "2px solid transparent",
              borderTopColor: "#3b82f6",
              borderRadius: "50%",
              width: "3rem",
              height: "3rem",
              animation: "spin 1s linear infinite",
              margin: "0 auto",
            }}
          ></div>
          <p style={{ marginTop: "1rem", color: "#6b7280" }}>Loading...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated && !useStyles) {
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
              className="w-full bg-amber-400 hover:bg-amber-200 font-medium py-3 px-4 rounded-lg transition-colors"
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

  if (!isAuthenticated && useStyles) {
    return (
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          minHeight: "100vh",
          background: "linear-gradient(to bottom right, #eff6ff, #e0e7ff)",
        }}
      >
        <div
          style={{
            backgroundColor: "white",
            padding: "2rem",
            borderRadius: "0.5rem",
            boxShadow: "0 10px 15px -3px rgba(0, 0, 0, 0.1)",
            maxWidth: "28rem",
            width: "100%",
          }}
        >
          <div style={{ textAlign: "center", marginBottom: "1.5rem" }}>
            <h1
              style={{
                fontSize: "1.875rem",
                fontWeight: "bold",
                color: "#111827",
                marginBottom: "0.5rem",
              }}
            >
              Smiles Dental Management
            </h1>
            <p style={{ color: "#6b7280" }}>
              Multi-Facility Dental Practice Management System
            </p>
          </div>

          <div>
            <button
              onClick={login}
              style={{
                width: "100%",
                backgroundColor: "#3b82f6",
                color: "white",
                fontWeight: "500",
                padding: "0.75rem 1rem",
                borderRadius: "0.5rem",
                border: "none",
                cursor: "pointer",
                transition: "background-color 0.2s",
              }}
              onMouseOver={(e) =>
                (e.currentTarget.style.backgroundColor = "#2563eb")
              }
              onMouseOut={(e) =>
                (e.currentTarget.style.backgroundColor = "#3b82f6")
              }
            >
              Sign In
            </button>

            <div
              style={{
                fontSize: "0.875rem",
                color: "#6b7280",
                textAlign: "center",
                marginTop: "1rem",
              }}
            >
              <p>Phase 0 - Development Environment</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Render current page content
  const renderPage = () => {
    switch (currentPage) {
      case "appointments":
        return <AppointmentsPage />;
      case "facilities":
        return <FacilityListPage />;
      case "staff":
        return <StaffListPage />;
      case "rooms":
        return <RoomListPage />;
      case "patients":
        return <PatientListPage />;
      default:
        return (
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
                    <dt className="text-sm font-medium text-gray-500">
                      Username
                    </dt>
                    <dd className="text-sm text-gray-900">{user?.username}</dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">Email</dt>
                    <dd className="text-sm text-gray-900">{user?.email}</dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">User ID</dt>
                    <dd className="text-sm text-gray-900 font-mono">
                      {user?.userId}
                    </dd>
                  </div>
                  <div>
                    <dt className="text-sm font-medium text-gray-500">
                      Email Verified
                    </dt>
                    <dd className="text-sm text-gray-900">
                      {user?.emailVerified ? "✅ Yes" : "❌ No"}
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
                          <dt className="text-sm font-medium text-gray-500">
                            {key}
                          </dt>
                          <dd className="text-sm text-gray-900">
                            {Array.isArray(value)
                              ? value.join(", ")
                              : String(value)}
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
                ✅ Phase 1 & 2 Complete!
              </h4>
              <p className="text-sm text-green-800">
                Phase 1: Core entities (Facility, Room, Staff, Patient) are implemented.
                <br />
                Phase 2: Basic appointment scheduling with conflict detection is ready!
              </p>
            </div>
          </div>
        );
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <h1
                className="text-xl font-bold text-gray-900 cursor-pointer hover:text-blue-600"
                onClick={() => setCurrentPage("home")}
              >
                Smiles Dental Management
              </h1>
              <div className="flex space-x-4">
                <button
                  onClick={() => setCurrentPage("appointments")}
                  className={`px-3 py-2 rounded-md text-sm font-medium ${
                    currentPage === "appointments"
                      ? "bg-blue-100 text-blue-900"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  Appointments
                </button>
                <button
                  onClick={() => setCurrentPage("facilities")}
                  className={`px-3 py-2 rounded-md text-sm font-medium ${
                    currentPage === "facilities"
                      ? "bg-blue-100 text-blue-900"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  Facilities
                </button>
                <button
                  onClick={() => setCurrentPage("staff")}
                  className={`px-3 py-2 rounded-md text-sm font-medium ${
                    currentPage === "staff"
                      ? "bg-blue-100 text-blue-900"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  Staff
                </button>
                <button
                  onClick={() => setCurrentPage("rooms")}
                  className={`px-3 py-2 rounded-md text-sm font-medium ${
                    currentPage === "rooms"
                      ? "bg-blue-100 text-blue-900"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  Rooms
                </button>
                <button
                  onClick={() => setCurrentPage("patients")}
                  className={`px-3 py-2 rounded-md text-sm font-medium ${
                    currentPage === "patients"
                      ? "bg-blue-100 text-blue-900"
                      : "text-gray-700 hover:bg-gray-100"
                  }`}
                >
                  Patients
                </button>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <div className="text-sm text-gray-700">
                <span className="font-medium">
                  {user?.fullName || user?.username}
                </span>
                <span className="ml-2 text-gray-500">
                  ({user?.roles.join(", ")})
                </span>
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

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">{renderPage()}</main>
    </div>
  );
};

export default HomePage;
