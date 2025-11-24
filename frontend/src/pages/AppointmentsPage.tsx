import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { facilityService } from '@/services/facility.service';
import { AppointmentCalendar } from '@/components/AppointmentCalendar';
import { CreateAppointmentModal } from '@/components/CreateAppointmentModal';

/**
 * Page component for managing appointments.
 */
export default function AppointmentsPage() {
  const [selectedFacilityId, setSelectedFacilityId] = useState<string>('');
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  // Fetch all facilities for selection
  const { data: facilities, isLoading: facilitiesLoading } = useQuery({
    queryKey: ['facilities'],
    queryFn: () => facilityService.getAllFacilities(),
  });

  // Auto-select first facility if available
  if (facilities && facilities.length > 0 && !selectedFacilityId) {
    setSelectedFacilityId(facilities[0].id);
  }

  if (facilitiesLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">Loading...</div>
      </div>
    );
  }

  if (!facilities || facilities.length === 0) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg text-gray-500">
          No facilities found. Please create a facility first.
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* Facility selector */}
      {facilities.length > 1 && (
        <div className="mb-6">
          <label htmlFor="facility" className="block text-sm font-medium text-gray-700 mb-2">
            Select Facility
          </label>
          <select
            id="facility"
            value={selectedFacilityId}
            onChange={(e) => setSelectedFacilityId(e.target.value)}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          >
            {facilities.map((facility) => (
              <option key={facility.id} value={facility.id}>
                {facility.name} - {facility.city}
              </option>
            ))}
          </select>
        </div>
      )}

      {/* Calendar */}
      {selectedFacilityId && (
        <AppointmentCalendar
          facilityId={selectedFacilityId}
          onCreateAppointment={() => setIsCreateModalOpen(true)}
        />
      )}

      {/* Create appointment modal */}
      <CreateAppointmentModal
        facilityId={selectedFacilityId}
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />
    </div>
  );
}
