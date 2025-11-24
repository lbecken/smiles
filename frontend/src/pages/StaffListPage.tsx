import { useQuery } from '@tanstack/react-query';
import { staffService } from '@/services/staff.service';
import { facilityService } from '@/services/facility.service';
import { Users, Plus } from 'lucide-react';
import { useState } from 'react';

/**
 * Page component for listing staff per facility.
 */
export default function StaffListPage() {
  const [selectedFacilityId, setSelectedFacilityId] = useState<string>('');

  const { data: facilities } = useQuery({
    queryKey: ['facilities'],
    queryFn: () => facilityService.getAllFacilities(),
  });

  const {
    data: staff,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['staff', selectedFacilityId],
    queryFn: () => staffService.getStaffByFacility(selectedFacilityId),
    enabled: !!selectedFacilityId,
  });

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Users className="w-8 h-8" />
          Staff
        </h1>
        <button
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
          disabled={!selectedFacilityId}
        >
          <Plus className="w-5 h-5" />
          Add Staff
        </button>
      </div>

      <div className="mb-6">
        <label htmlFor="facility-select" className="block text-sm font-medium text-gray-700 mb-2">
          Select Facility
        </label>
        <select
          id="facility-select"
          value={selectedFacilityId}
          onChange={(e) => setSelectedFacilityId(e.target.value)}
          className="block w-full max-w-md px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
        >
          <option value="">-- Select a facility --</option>
          {facilities?.map((facility) => (
            <option key={facility.id} value={facility.id}>
              {facility.name}
            </option>
          ))}
        </select>
      </div>

      {isLoading && <div className="text-center py-12">Loading staff...</div>}

      {error && (
        <div className="text-center py-12 text-red-600">
          Error loading staff: {error instanceof Error ? error.message : 'Unknown error'}
        </div>
      )}

      {selectedFacilityId && !isLoading && (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {staff?.map((member) => (
            <div
              key={member.id}
              className="p-6 bg-white rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow"
            >
              <h2 className="text-xl font-semibold mb-2">{member.name}</h2>
              <div className="text-gray-600 space-y-1">
                <p>
                  <span className="font-medium">Email:</span> {member.email}
                </p>
                <p>
                  <span className="font-medium">Role:</span>{' '}
                  <span className="capitalize">{member.role.toLowerCase()}</span>
                </p>
                <p>
                  <span className="font-medium">Status:</span>{' '}
                  <span className={member.active ? 'text-green-600' : 'text-red-600'}>
                    {member.active ? 'Active' : 'Inactive'}
                  </span>
                </p>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedFacilityId && staff?.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          No staff found for this facility. Add your first staff member to get started.
        </div>
      )}
    </div>
  );
}
