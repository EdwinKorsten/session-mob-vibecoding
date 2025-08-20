'use client';

import { useState, useEffect } from 'react';
import { ChargingPlanRequest } from '@/types';
import { getStoredValue, setStoredValue, STORAGE_KEYS } from '@/utils/storage';

interface ChargingFormProps {
  onSubmit: (request: ChargingPlanRequest) => void;
  isLoading: boolean;
}

export default function ChargingForm({ onSubmit, isLoading }: ChargingFormProps) {
  const [chargeRate, setChargeRate] = useState('10');
  const [energyNeeded, setEnergyNeeded] = useState('80');
  const [deadline, setDeadline] = useState('14:00');
  const [continuous, setContinuous] = useState(true);

  useEffect(() => {
    setChargeRate(getStoredValue(STORAGE_KEYS.CHARGE_RATE, '10'));
    setEnergyNeeded(getStoredValue(STORAGE_KEYS.ENERGY_NEEDED, '80'));
    setDeadline(getStoredValue(STORAGE_KEYS.DEADLINE, '14:00'));
    setContinuous(getStoredValue(STORAGE_KEYS.MODE, 'true') === 'true');
  }, []);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    setStoredValue(STORAGE_KEYS.CHARGE_RATE, chargeRate);
    setStoredValue(STORAGE_KEYS.ENERGY_NEEDED, energyNeeded);
    setStoredValue(STORAGE_KEYS.DEADLINE, deadline);
    setStoredValue(STORAGE_KEYS.MODE, continuous.toString());
    
    const today = new Date();
    const request: ChargingPlanRequest = {
      date: today.toISOString().split('T')[0],
      deadline,
      timezone: 'Europe/Amsterdam',
      chargeRateKwhPerHour: parseFloat(chargeRate),
      energyNeededKwh: parseFloat(energyNeeded),
      continuous,
    };
    
    onSubmit(request);
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6 bg-white p-6 rounded-lg shadow">
      <h2 className="text-xl font-semibold text-gray-900">Charging Plan Calculator</h2>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div className="flex flex-col space-y-2">
          <label htmlFor="charge-rate" className="text-sm font-medium text-gray-900">
            Charge Rate (kWh/h)
          </label>
          <input
            id="charge-rate"
            type="number"
            min="0.1"
            max="350"
            step="0.1"
            value={chargeRate}
            onChange={(e) => setChargeRate(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            style={{ color: '#000000' }}
            aria-label="Charging rate in kilowatts per hour"
          />
        </div>
        
        <div className="flex flex-col space-y-2">
          <label htmlFor="energy-needed" className="text-sm font-medium text-gray-900">
            Energy Needed (kWh)
          </label>
          <input
            id="energy-needed"
            type="number"
            min="1"
            max="1000"
            step="1"
            value={energyNeeded}
            onChange={(e) => setEnergyNeeded(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            style={{ color: '#000000' }}
            aria-label="Energy needed in kilowatt hours"
          />
        </div>
        
        <div className="flex flex-col space-y-2">
          <label htmlFor="deadline" className="text-sm font-medium text-gray-900">
            Finish By
          </label>
          <input
            id="deadline"
            type="time"
            value={deadline}
            onChange={(e) => setDeadline(e.target.value)}
            className="px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            style={{ color: '#000000' }}
            aria-label="Charging deadline time"
          />
        </div>
        
        <div className="flex flex-col space-y-2">
          <label className="text-sm font-medium text-gray-900">
            Charging Mode
          </label>
          <div className="flex space-x-4">
            <label className="flex items-center text-gray-900">
              <input
                type="radio"
                name="mode"
                checked={continuous}
                onChange={() => setContinuous(true)}
                className="mr-2"
              />
              Continuous
            </label>
            <label className="flex items-center text-gray-900">
              <input
                type="radio"
                name="mode"
                checked={!continuous}
                onChange={() => setContinuous(false)}
                className="mr-2"
              />
              Discrete
            </label>
          </div>
        </div>
      </div>
      
      <button
        type="submit"
        disabled={isLoading}
        className="w-full bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      >
        {isLoading ? 'Calculating...' : 'Calculate Optimal Plan'}
      </button>
    </form>
  );
}