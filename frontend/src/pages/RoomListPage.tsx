import { useQuery } from '@tanstack/react-query';
import { roomService } from '@/services/room.service';
import { facilityService } from '@/services/facility.service';
import { Door, Plus } from 'lucide-react';
import { useState } from 'react';

/**
 * Page component for listing rooms per facility.
 */
export default function RoomListPage() {
  const [selectedFacilityId, setSelectedFacilityId] = useState<string>('');

  const { data: facilities } = useQuery({
    queryKey: ['facilities'],
    queryFn: () => facilityService.getAllFacilities(),
  });

  const {
    data: rooms,
    isLoading,
    error,
  } = useQuery({
    queryKey: ['rooms', selectedFacilityId],
    queryFn: () => roomService.getRoomsByFacility(selectedFacilityId),
    enabled: !!selectedFacilityId,
  });

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Door className="w-8 h-8" />
          Rooms
        </h1>
        <button
          className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400"
          disabled={!selectedFacilityId}
        >
          <Plus className="w-5 h-5" />
          Add Room
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

      {isLoading && <div className="text-center py-12">Loading rooms...</div>}

      {error && (
        <div className="text-center py-12 text-red-600">
          Error loading rooms: {error instanceof Error ? error.message : 'Unknown error'}
        </div>
      )}

      {selectedFacilityId && !isLoading && (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          {rooms?.map((room) => (
            <div
              key={room.id}
              className="p-6 bg-white rounded-lg shadow-md border border-gray-200 hover:shadow-lg transition-shadow"
            >
              <h2 className="text-xl font-semibold mb-2">{room.name}</h2>
              <div className="text-gray-600">
                <p>
                  <span className="font-medium">Type:</span>{' '}
                  <span className="capitalize">{room.type.toLowerCase().replace('_', ' ')}</span>
                </p>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedFacilityId && rooms?.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          No rooms found for this facility. Add your first room to get started.
        </div>
      )}
    </div>
  );
}
