# Smiles Dental Management - Frontend

Modern React frontend for the Smiles Dental Multi-Facility Management System.

## Tech Stack

- **React**: 18
- **Vite**: Latest
- **TypeScript**: 5+
- **UI Library**: ShadCN UI (Tailwind CSS)
- **State Management**: React Query (TanStack Query)
- **Authentication**: Keycloak JS Adapter
- **HTTP Client**: Axios

## Getting Started

### Prerequisites

- Node.js 18+ and npm
- Backend and Docker services running (see root README)

### Installation

```bash
# Install dependencies
npm install

# Copy environment variables
cp .env.example .env

# Start development server
npm run dev
```

The frontend will be available at `http://localhost:5173`

## Testing Authentication

Once the application is running:

1. Navigate to `http://localhost:5173`
2. Click "Sign In"
3. Use one of the test accounts:
   - **Admin**: admin / admin123
   - **Dentist**: dr.smith / dentist123
   - **Staff**: jane.doe / staff123
   - **Patient**: patient.test / patient123

## Environment Variables

Create a `.env` file from `.env.example`:

```env
VITE_API_BASE_URL=http://localhost:8081/api
```
