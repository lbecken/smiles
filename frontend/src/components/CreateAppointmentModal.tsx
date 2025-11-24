import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { X, AlertCircle } from 'lucide-react';
import { appointmentService } from '@/services/appointment.service';
import { staffService } from '@/services/staff.service';
import { roomService } from '@/services/room.service';
import { patientService } from '@/services/patient.service';
import type { CreateAppointmentRequest } from '@/types/appointment';
import { StaffRole } from '@/types/staff';

interface CreateAppointmentModalProps {
  facilityId: string;
  isOpen: boolean;
  onClose: () => void;
}

/**
 * Modal component for creating a new appointment.
 */
export function CreateAppointmentModal({ facilityId, isOpen, onClose }: CreateAppointmentModalProps) {
  const queryClient = useQueryClient();
  const [formData, setFormData] = useState({
    patientId: '',
    dentistId: '',
    roomId: '',
    startDate: '',
    startTime: '',
    duration: '60', // minutes
  });
  const [error, setError] = useState<string | null>(null);

  // Fetch dentists for the facility
  const { data: staff } = useQuery({
    queryKey: ['staff', facilityId],
    queryFn: () => staffService.getStaffByFacility(facilityId),
    enabled: isOpen,
  });

  const dentists = staff?.filter(s => s.role === StaffRole.DENTIST && s.active) || [];

  // Fetch rooms for the facility
  const { data: rooms } = useQuery({
    queryKey: ['rooms', facilityId],
    queryFn: () => roomService.getRoomsByFacility(facilityId),
    enabled: isOpen,
  });

  // Fetch patients for the facility
  const { data: patients } = useQuery({
    queryKey: ['patients', facilityId],
    queryFn: () => patientService.getPatientsByFacility(facilityId),
    enabled: isOpen,
  });

  // Create appointment mutation
  const createMutation = useMutation({
    mutationFn: (request: CreateAppointmentRequest) => appointmentService.createAppointment(request),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['appointments'] });
      onClose();
      resetForm();
    },
    onError: (error: any) => {
      if (error.response?.status === 409) {
        setError('Conflict: Dentist or room is already booked at this time.');
      } else if (error.response?.status === 400) {
        setError('Invalid appointment data. Please check all fields.');
      } else {
        setError('Failed to create appointment. Please try again.');
      }
    },
  });

  const resetForm = () => {
    setFormData({
      patientId: '',
      dentistId: '',
      roomId: '',
      startDate: '',
      startTime: '',
      duration: '60',
    });
    setError(null);
  };

  useEffect(() => {
    if (!isOpen) {
      resetForm();
    }
  }, [isOpen]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    // Validate form
    if (!formData.patientId || !formData.dentistId || !formData.roomId || !formData.startDate || !formData.startTime) {
      setError('Please fill in all required fields.');
      return;
    }

    // Calculate start and end times
    const [hours, minutes] = formData.startTime.split(':');
    const startDateTime = new Date(formData.startDate);
    startDateTime.setHours(parseInt(hours), parseInt(minutes), 0, 0);

    const endDateTime = new Date(startDateTime);
    endDateTime.setMinutes(endDateTime.getMinutes() + parseInt(formData.duration));

    const request: CreateAppointmentRequest = {
      patientId: formData.patientId,
      dentistId: formData.dentistId,
      roomId: formData.roomId,
      facilityId,
      startTime: startDateTime.toISOString(),
      endTime: endDateTime.toISOString(),
    };

    createMutation.mutate(request);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <h2 className="text-2xl font-bold">Create Appointment</h2>
          <button
            onClick={onClose}
            className="p-2 hover:bg-gray-100 rounded-full"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-6 space-y-4">
          {error && (
            <div className="p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-2">
              <AlertCircle className="w-5 h-5 text-red-600 flex-shrink-0 mt-0.5" />
              <p className="text-red-800">{error}</p>
            </div>
          )}

          {/* Patient Selection */}
          <div>
            <label htmlFor="patient" className="block text-sm font-medium text-gray-700 mb-1">
              Patient *
            </label>
            <select
              id="patient"
              value={formData.patientId}
              onChange={(e) => setFormData({ ...formData, patientId: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="">Select a patient</option>
              {patients?.filter(p => p.active).map((patient) => (
                <option key={patient.id} value={patient.id}>
                  {patient.name} - {patient.email}
                </option>
              ))}
            </select>
          </div>

          {/* Dentist Selection */}
          <div>
            <label htmlFor="dentist" className="block text-sm font-medium text-gray-700 mb-1">
              Dentist *
            </label>
            <select
              id="dentist"
              value={formData.dentistId}
              onChange={(e) => setFormData({ ...formData, dentistId: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="">Select a dentist</option>
              {dentists.map((dentist) => (
                <option key={dentist.id} value={dentist.id}>
                  {dentist.name}
                </option>
              ))}
            </select>
          </div>

          {/* Room Selection */}
          <div>
            <label htmlFor="room" className="block text-sm font-medium text-gray-700 mb-1">
              Room *
            </label>
            <select
              id="room"
              value={formData.roomId}
              onChange={(e) => setFormData({ ...formData, roomId: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="">Select a room</option>
              {rooms?.map((room) => (
                <option key={room.id} value={room.id}>
                  {room.name} ({room.type})
                </option>
              ))}
            </select>
          </div>

          {/* Date */}
          <div>
            <label htmlFor="startDate" className="block text-sm font-medium text-gray-700 mb-1">
              Date *
            </label>
            <input
              type="date"
              id="startDate"
              value={formData.startDate}
              onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            />
          </div>

          {/* Time and Duration */}
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label htmlFor="startTime" className="block text-sm font-medium text-gray-700 mb-1">
                Start Time *
              </label>
              <input
                type="time"
                id="startTime"
                value={formData.startTime}
                onChange={(e) => setFormData({ ...formData, startTime: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              />
            </div>

            <div>
              <label htmlFor="duration" className="block text-sm font-medium text-gray-700 mb-1">
                Duration (minutes) *
              </label>
              <select
                id="duration"
                value={formData.duration}
                onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
              >
                <option value="30">30 minutes</option>
                <option value="60">1 hour</option>
                <option value="90">1.5 hours</option>
                <option value="120">2 hours</option>
              </select>
            </div>
          </div>

          {/* Actions */}
          <div className="flex justify-end gap-3 pt-4 border-t border-gray-200">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={createMutation.isPending}
              className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed"
            >
              {createMutation.isPending ? 'Creating...' : 'Create Appointment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
