'use client';

import { format, parseISO } from 'date-fns';
import { ChargingPlan } from '@/types';
import { Clock, Zap, Euro } from 'lucide-react';

interface PlanSummaryProps {
  plan: ChargingPlan | null;
}

export default function PlanSummary({ plan }: PlanSummaryProps) {
  if (!plan) {
    return (
      <div className="bg-gray-50 p-6 rounded-lg text-center text-gray-500">
        Click &quot;Calculate Optimal Plan&quot; to see your charging recommendation
      </div>
    );
  }

  const formatTime = (timeString: string) => {
    try {
      return format(parseISO(timeString), 'HH:mm');
    } catch {
      return timeString;
    }
  };

  const formatDateTime = (timeString: string) => {
    try {
      return format(parseISO(timeString), 'MMM d, HH:mm');
    } catch {
      return timeString;
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow space-y-4">
      <h2 className="text-xl font-semibold text-gray-900">
        Optimal Charging Plan ({plan.mode})
      </h2>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="flex items-center space-x-3 p-3 bg-blue-50 rounded-lg">
          <Clock className="h-5 w-5 text-blue-600" />
          <div>
            <p className="text-sm font-medium text-gray-600">Start Time</p>
            <p className="text-lg font-semibold text-gray-900">
              {plan.startTime ? formatTime(plan.startTime) : 'N/A'}
            </p>
          </div>
        </div>
        
        <div className="flex items-center space-x-3 p-3 bg-green-50 rounded-lg">
          <Zap className="h-5 w-5 text-green-600" />
          <div>
            <p className="text-sm font-medium text-gray-600">Hours Needed</p>
            <p className="text-lg font-semibold text-gray-900">{plan.requiredHours}h</p>
          </div>
        </div>
        
        <div className="flex items-center space-x-3 p-3 bg-yellow-50 rounded-lg">
          <Euro className="h-5 w-5 text-yellow-600" />
          <div>
            <p className="text-sm font-medium text-gray-600">Total Cost</p>
            <p className="text-lg font-semibold text-gray-900">
              €{plan.totalCostEur.toFixed(2)}
            </p>
          </div>
        </div>
      </div>
      
      <div className="border-t pt-4">
        <h3 className="font-medium text-gray-900 mb-3">Selected Hours</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 lg:grid-cols-6 gap-2">
          {plan.selectedHours.map((hour, index) => (
            <div
              key={index}
              className="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm text-center"
            >
              {formatTime(hour)}
            </div>
          ))}
        </div>
      </div>
      
      <div className="text-sm text-gray-600 space-y-1">
        <p>
          Average price: €{plan.avgPriceEurPerKwh.toFixed(3)}/kWh
        </p>
        <p>
          Must finish by: {formatDateTime(plan.deadline)}
        </p>
      </div>
    </div>
  );
}