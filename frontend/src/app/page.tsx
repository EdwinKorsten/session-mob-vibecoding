'use client';

import { useState } from 'react';
import { format } from 'date-fns';
import DatePicker from '@/components/DatePicker';
import PriceChart from '@/components/PriceChart';
import ChargingForm from '@/components/ChargingForm';
import PlanSummary from '@/components/PlanSummary';
import { usePrices, useChargingPlanMutation } from '@/hooks/useApi';
import { ChargingPlan, ChargingPlanRequest } from '@/types';
import { Zap } from 'lucide-react';

export default function Home() {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [chargingPlan, setChargingPlan] = useState<ChargingPlan | null>(null);
  
  const dateString = format(selectedDate, 'yyyy-MM-dd');
  const { data: priceData, isLoading: isPricesLoading, error: pricesError } = usePrices(dateString);
  const chargingPlanMutation = useChargingPlanMutation();

  const handleDateChange = (date: Date) => {
    setSelectedDate(date);
    setChargingPlan(null);
  };

  const handleChargingPlanRequest = async (request: ChargingPlanRequest) => {
    const updatedRequest = {
      ...request,
      date: dateString,
    };
    
    try {
      const plan = await chargingPlanMutation.mutateAsync(updatedRequest);
      setChargingPlan(plan);
    } catch (error) {
      console.error('Failed to calculate charging plan:', error);
      setChargingPlan(null);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center space-x-3">
            <Zap className="h-8 w-8 text-blue-600" />
            <div>
              <h1 className="text-2xl font-bold text-gray-900">EV Charging Optimizer</h1>
              <p className="text-gray-600">Find the cheapest times to charge your electric vehicle</p>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="space-y-8">
          <div className="bg-white p-6 rounded-lg shadow">
            <DatePicker value={selectedDate} onChange={handleDateChange} />
            {pricesError && (
              <div className="mt-4 p-4 bg-red-50 border border-red-200 rounded-md">
                <p className="text-red-800">Failed to load price data. Please try again.</p>
              </div>
            )}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
            <div className="space-y-8">
              <ChargingForm
                onSubmit={handleChargingPlanRequest}
                isLoading={chargingPlanMutation.isPending}
              />
              
              {chargingPlanMutation.error && (
                <div className="p-4 bg-red-50 border border-red-200 rounded-md">
                  <p className="text-red-800">
                    {chargingPlanMutation.error.message || 'Failed to calculate charging plan'}
                  </p>
                </div>
              )}
              
              <PlanSummary plan={chargingPlan} />
            </div>

            <div>
              <PriceChart
                data={priceData || []}
                plan={chargingPlan}
                isLoading={isPricesLoading}
              />
            </div>
          </div>
        </div>
      </main>

      <footer className="bg-white border-t border-gray-200 mt-16">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <p className="text-center text-gray-900">
            Powered by Energy Charts API • Times in Europe/Amsterdam timezone
          </p>
        </div>
      </footer>
    </div>
  );
}