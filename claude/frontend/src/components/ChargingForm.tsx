'use client';

import { useState, useEffect } from 'react';
import { useMutation } from '@tanstack/react-query';
import { format } from 'date-fns';
import { calculatePlan } from '@/api/client';
import { ChargingPlanRequest, ChargingPlanResponse } from '@/types';

interface ChargingFormProps {
  selectedDate: Date;
  onPlanCalculated: (plan: ChargingPlanResponse) => void;
}

export default function ChargingForm({ selectedDate, onPlanCalculated }: ChargingFormProps) {
  const [deadline, setDeadline] = useState('14:00');
  const [chargeRate, setChargeRate] = useState(10);
  const [energyNeeded, setEnergyNeeded] = useState(80);
  const [continuous, setContinuous] = useState(true);

  useEffect(() => {
    const savedValues = localStorage.getItem('chargingFormValues');
    if (savedValues) {
      const parsed = JSON.parse(savedValues);
      setDeadline(parsed.deadline || '14:00');
      setChargeRate(parsed.chargeRate || 10);
      setEnergyNeeded(parsed.energyNeeded || 80);
      setContinuous(parsed.continuous !== undefined ? parsed.continuous : true);
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('chargingFormValues', JSON.stringify({
      deadline,
      chargeRate,
      energyNeeded,
      continuous
    }));
  }, [deadline, chargeRate, energyNeeded, continuous]);

  const mutation = useMutation({
    mutationFn: calculatePlan,
    onSuccess: (data) => {
      onPlanCalculated(data);
    },
    onError: (error) => {
      console.error('Error calculating plan:', error);
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    const request: ChargingPlanRequest = {
      date: format(selectedDate, 'yyyy-MM-dd'),
      deadline,
      timezone: 'Europe/Amsterdam',
      chargeRateKwhPerHour: chargeRate,
      energyNeededKwh: energyNeeded,
      continuous,
    };

    mutation.mutate(request);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div>
        <label htmlFor="deadline" className="block text-sm font-medium text-gray-700 mb-1">
          Finish by (24h format):
        </label>
        <input
          type="time"
          id="deadline"
          value={deadline}
          onChange={(e) => setDeadline(e.target.value)}
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          required
        />
      </div>

      <div>
        <label htmlFor="chargeRate" className="block text-sm font-medium text-gray-700 mb-1">
          Charge rate (kWh/h):
        </label>
        <input
          type="number"
          id="chargeRate"
          value={chargeRate}
          onChange={(e) => setChargeRate(Number(e.target.value))}
          min="0.1"
          step="0.1"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          required
        />
      </div>

      <div>
        <label htmlFor="energyNeeded" className="block text-sm font-medium text-gray-700 mb-1">
          Energy needed (kWh):
        </label>
        <input
          type="number"
          id="energyNeeded"
          value={energyNeeded}
          onChange={(e) => setEnergyNeeded(Number(e.target.value))}
          min="0.1"
          step="0.1"
          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          required
        />
      </div>

      <div>
        <label className="block text-sm font-medium text-gray-700 mb-2">
          Charging mode:
        </label>
        <div className="space-y-2">
          <label className="flex items-center">
            <input
              type="radio"
              name="mode"
              value="continuous"
              checked={continuous}
              onChange={() => setContinuous(true)}
              className="mr-2"
            />
            <span className="text-sm">Continuous (consecutive hours)</span>
          </label>
          <label className="flex items-center">
            <input
              type="radio"
              name="mode"
              value="discrete"
              checked={!continuous}
              onChange={() => setContinuous(false)}
              className="mr-2"
            />
            <span className="text-sm">Discrete (cheapest hours)</span>
          </label>
        </div>
      </div>

      <button
        type="submit"
        disabled={mutation.isPending}
        className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {mutation.isPending ? 'Calculating...' : 'Calculate Plan'}
      </button>

      {mutation.isError && (
        <div className="text-red-600 text-sm">
          Error calculating plan. Please try again.
        </div>
      )}
    </form>
  );
}