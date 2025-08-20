'use client';

import { useState, useEffect } from 'react';
import { format } from 'date-fns';
import DatePicker from './DatePicker';
import PriceChart from './PriceChart';
import ChargingForm from './ChargingForm';
import PlanSummary from './PlanSummary';
import { ChargingPlanResponse } from '@/types';

export default function Dashboard() {
  const [selectedDate, setSelectedDate] = useState<Date>(new Date());
  const [plan, setPlan] = useState<ChargingPlanResponse | null>(null);

  const handlePlanCalculated = (newPlan: ChargingPlanResponse) => {
    setPlan(newPlan);
  };

  const formattedDate = format(selectedDate, 'yyyy-MM-dd');

  return (
    <div className="space-y-8">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h2 className="text-xl font-semibold mb-4">Date Selection</h2>
            <DatePicker
              selectedDate={selectedDate}
              onDateChange={setSelectedDate}
            />
          </div>
          
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h2 className="text-xl font-semibold mb-4">Hourly Electricity Prices</h2>
            <PriceChart 
              date={formattedDate} 
              selectedHours={plan?.selectedHours || []}
            />
          </div>
        </div>
        
        <div className="space-y-6">
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h2 className="text-xl font-semibold mb-4">Charging Plan</h2>
            <ChargingForm
              selectedDate={selectedDate}
              onPlanCalculated={handlePlanCalculated}
            />
          </div>
          
          {plan && (
            <div className="bg-white rounded-lg shadow-sm p-6">
              <h2 className="text-xl font-semibold mb-4">Plan Summary</h2>
              <PlanSummary plan={plan} />
            </div>
          )}
        </div>
      </div>
    </div>
  );
}