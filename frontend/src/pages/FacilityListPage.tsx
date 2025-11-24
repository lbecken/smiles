import { useQuery } from '@tanstack/react-query';
import { facilityService } from '@/services/facility.service';
import { Building2, Plus } from 'lucide-react';

/**
 * Page component for listing facilities (admin only).
 */
export default function FacilityListPage() {
  const { data: facilities, isLoading, error } = useQuery({
    queryKey: ['facilities'],
    queryFn: () => facilityService.getAllFacilities(),
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg">Loading facilities...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-lg text-red-600">
          Error loading facilities: {error instanceof Error ? error.message : 'Unknown error'}
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Building2 className="w-8 h-8" />
          Facilities
        </h1>
        <button className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
          <Plus className="w-5 h-5" />
          Add Facility
        </button>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {facilities?.map((facility) => (
          <div
            key={facility.id}
            className="p-6 bg-white rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow"
          >
            <h2 className="text-xl font-semibold mb-2">{facility.name}</h2>
            <div className="text-gray-600 space-y-1">
              <p>
                <span className="font-medium">City:</span> {facility.city}
              </p>
              <p>
                <span className="font-medium">Address:</span> {facility.address}
              </p>
            </div>
          </div>
        ))}
      </div>

      {facilities?.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          No facilities found. Add your first facility to get started.
        </div>
      )}
    </div>
  );
}
