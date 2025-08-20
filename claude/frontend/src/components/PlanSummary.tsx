import { format, parseISO } from 'date-fns';
import { ChargingPlanResponse } from '@/types';

interface PlanSummaryProps {
  plan: ChargingPlanResponse;
}

export default function PlanSummary({ plan }: PlanSummaryProps) {
  const formatTime = (timestamp: string) => {
    return format(parseISO(timestamp), 'HH:mm');
  };

  const sortedHours = plan.selectedHours
    .map(hour => ({ time: formatTime(hour), timestamp: hour }))
    .sort((a, b) => a.timestamp.localeCompare(b.timestamp));

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-1 gap-3">
        <div className="bg-gray-50 p-3 rounded">
          <div className="text-sm font-medium text-gray-700">Mode</div>
          <div className="text-lg font-semibold">
            {plan.mode === 'CONTINUOUS' ? 'Continuous' : 'Discrete'}
          </div>
        </div>

        <div className="bg-gray-50 p-3 rounded">
          <div className="text-sm font-medium text-gray-700">Required Hours</div>
          <div className="text-lg font-semibold">{plan.requiredHours}</div>
        </div>

        <div className="bg-gray-50 p-3 rounded">
          <div className="text-sm font-medium text-gray-700">
            {plan.mode === 'CONTINUOUS' ? 'Start Time' : 'First Hour'}
          </div>
          <div className="text-lg font-semibold">{formatTime(plan.startTime)}</div>
        </div>

        <div className="bg-gray-50 p-3 rounded">
          <div className="text-sm font-medium text-gray-700">Total Cost</div>
          <div className="text-lg font-semibold text-green-600">
            €{plan.totalCostEur.toFixed(2)}
          </div>
        </div>

        <div className="bg-gray-50 p-3 rounded">
          <div className="text-sm font-medium text-gray-700">Average Price</div>
          <div className="text-lg font-semibold">
            €{plan.avgPriceEurPerKwh.toFixed(3)}/kWh
          </div>
        </div>
      </div>

      <div className="border-t pt-4">
        <div className="text-sm font-medium text-gray-700 mb-2">Selected Hours:</div>
        <div className="grid grid-cols-3 gap-1">
          {sortedHours.map((hour, index) => (
            <div
              key={index}
              className="bg-green-100 text-green-800 px-2 py-1 rounded text-sm text-center"
            >
              {hour.time}
            </div>
          ))}
        </div>
      </div>

      <div className="text-xs text-gray-500">
        Deadline: {formatTime(plan.deadline)}
      </div>
    </div>
  );
}