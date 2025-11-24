import { useState, useMemo } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Calendar, ChevronLeft, ChevronRight, Plus } from 'lucide-react';
import { appointmentService } from '@/services/appointment.service';
import type { Appointment } from '@/types/appointment';
import { format, addDays, startOfWeek, addWeeks, isSameDay, parseISO } from 'date-fns';

interface AppointmentCalendarProps {
  facilityId: string;
  onCreateAppointment: () => void;
}

type ViewMode = 'day' | 'week';

/**
 * Calendar component for displaying appointments.
 */
export function AppointmentCalendar({ facilityId, onCreateAppointment }: AppointmentCalendarProps) {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [viewMode, setViewMode] = useState<ViewMode>('week');

  // Calculate date range based on view mode
  const { startDate, endDate, days } = useMemo(() => {
    if (viewMode === 'day') {
      return {
        startDate: currentDate,
        endDate: addDays(currentDate, 1),
        days: [currentDate]
      };
    } else {
      const weekStart = startOfWeek(currentDate, { weekStartsOn: 1 }); // Start on Monday
      return {
        startDate: weekStart,
        endDate: addDays(weekStart, 7),
        days: Array.from({ length: 7 }, (_, i) => addDays(weekStart, i))
      };
    }
  }, [currentDate, viewMode]);

  // Fetch appointments for the current date range
  const { data: appointments, isLoading } = useQuery({
    queryKey: ['appointments', facilityId, startDate.toISOString(), endDate.toISOString()],
    queryFn: () => appointmentService.getAppointmentsByFacility(
      facilityId,
      startDate.toISOString(),
      endDate.toISOString()
    ),
  });

  // Navigate to previous period
  const handlePrevious = () => {
    if (viewMode === 'day') {
      setCurrentDate(addDays(currentDate, -1));
    } else {
      setCurrentDate(addWeeks(currentDate, -1));
    }
  };

  // Navigate to next period
  const handleNext = () => {
    if (viewMode === 'day') {
      setCurrentDate(addDays(currentDate, 1));
    } else {
      setCurrentDate(addWeeks(currentDate, 1));
    }
  };

  // Group appointments by day and time slot
  const appointmentsByDay = useMemo(() => {
    if (!appointments) return new Map<string, Appointment[]>();

    const map = new Map<string, Appointment[]>();
    appointments.forEach(apt => {
      const dateKey = format(parseISO(apt.startTime), 'yyyy-MM-dd');
      const existing = map.get(dateKey) || [];
      map.set(dateKey, [...existing, apt]);
    });

    // Sort appointments by start time within each day
    map.forEach((apts, key) => {
      map.set(key, apts.sort((a, b) =>
        new Date(a.startTime).getTime() - new Date(b.startTime).getTime()
      ));
    });

    return map;
  }, [appointments]);

  // Time slots for the calendar (8 AM to 6 PM)
  const timeSlots = Array.from({ length: 11 }, (_, i) => i + 8);

  return (
    <div className="bg-white rounded-lg shadow-md p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center gap-4">
          <h2 className="text-2xl font-bold flex items-center gap-2">
            <Calendar className="w-6 h-6" />
            Appointments
          </h2>
          <div className="flex gap-2">
            <button
              onClick={() => setViewMode('day')}
              className={`px-3 py-1 rounded ${
                viewMode === 'day'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Day
            </button>
            <button
              onClick={() => setViewMode('week')}
              className={`px-3 py-1 rounded ${
                viewMode === 'week'
                  ? 'bg-blue-600 text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Week
            </button>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <button
            onClick={handlePrevious}
            className="p-2 hover:bg-gray-100 rounded-full"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>

          <div className="text-lg font-semibold min-w-[200px] text-center">
            {viewMode === 'day'
              ? format(currentDate, 'EEEE, MMMM d, yyyy')
              : `${format(startDate, 'MMM d')} - ${format(addDays(endDate, -1), 'MMM d, yyyy')}`
            }
          </div>

          <button
            onClick={handleNext}
            className="p-2 hover:bg-gray-100 rounded-full"
          >
            <ChevronRight className="w-5 h-5" />
          </button>

          <button
            onClick={() => setCurrentDate(new Date())}
            className="px-3 py-1 bg-gray-200 text-gray-700 rounded hover:bg-gray-300"
          >
            Today
          </button>

          <button
            onClick={onCreateAppointment}
            className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
          >
            <Plus className="w-5 h-5" />
            New Appointment
          </button>
        </div>
      </div>

      {/* Calendar Grid */}
      {isLoading ? (
        <div className="text-center py-12 text-gray-500">Loading appointments...</div>
      ) : (
        <div className="overflow-x-auto">
          <div className="min-w-[800px]">
            {/* Day headers */}
            <div className="grid gap-1 mb-2" style={{ gridTemplateColumns: `80px repeat(${days.length}, 1fr)` }}>
              <div className="font-semibold text-gray-600 text-sm"></div>
              {days.map((day, index) => (
                <div
                  key={index}
                  className={`text-center p-2 rounded ${
                    isSameDay(day, new Date())
                      ? 'bg-blue-100 font-bold text-blue-900'
                      : 'font-semibold text-gray-700'
                  }`}
                >
                  <div className="text-xs">{format(day, 'EEE')}</div>
                  <div className="text-lg">{format(day, 'd')}</div>
                </div>
              ))}
            </div>

            {/* Time slots */}
            <div className="border border-gray-200 rounded">
              {timeSlots.map((hour) => (
                <div
                  key={hour}
                  className="grid gap-1 border-b last:border-b-0 border-gray-200"
                  style={{ gridTemplateColumns: `80px repeat(${days.length}, 1fr)` }}
                >
                  <div className="p-2 text-sm text-gray-600 font-medium border-r border-gray-200">
                    {format(new Date().setHours(hour, 0), 'h:mm a')}
                  </div>
                  {days.map((day, dayIndex) => {
                    const dateKey = format(day, 'yyyy-MM-dd');
                    const dayAppointments = appointmentsByDay.get(dateKey) || [];
                    const slotAppointments = dayAppointments.filter(apt => {
                      const startHour = new Date(apt.startTime).getHours();
                      return startHour === hour;
                    });

                    return (
                      <div
                        key={dayIndex}
                        className="p-1 min-h-[60px] border-r last:border-r-0 border-gray-200 hover:bg-gray-50"
                      >
                        {slotAppointments.map((apt) => (
                          <div
                            key={apt.id}
                            className={`text-xs p-2 rounded mb-1 ${
                              apt.status === 'SCHEDULED'
                                ? 'bg-blue-100 text-blue-900 border border-blue-300'
                                : apt.status === 'ONGOING'
                                ? 'bg-green-100 text-green-900 border border-green-300'
                                : apt.status === 'COMPLETED'
                                ? 'bg-gray-100 text-gray-700 border border-gray-300'
                                : 'bg-red-100 text-red-900 border border-red-300'
                            }`}
                          >
                            <div className="font-semibold">
                              {format(parseISO(apt.startTime), 'h:mm a')} - {format(parseISO(apt.endTime), 'h:mm a')}
                            </div>
                            <div className="text-xs mt-1">Patient: {apt.patientId.slice(0, 8)}...</div>
                          </div>
                        ))}
                      </div>
                    );
                  })}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Empty state */}
      {!isLoading && appointments?.length === 0 && (
        <div className="text-center py-12 text-gray-500">
          No appointments scheduled for this period.
        </div>
      )}
    </div>
  );
}
