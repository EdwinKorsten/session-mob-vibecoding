import { PricePoint, ChargingPlanRequest, ChargingPlanResponse } from '@/types';

const API_BASE = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const fetchPrices = async (date: string): Promise<PricePoint[]> => {
  const response = await fetch(`${API_BASE}/prices?date=${date}`);
  if (!response.ok) {
    throw new Error('Failed to fetch prices');
  }
  return response.json();
};

export const calculatePlan = async (request: ChargingPlanRequest): Promise<ChargingPlanResponse> => {
  const response = await fetch(`${API_BASE}/plan`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });
  
  if (!response.ok) {
    throw new Error('Failed to calculate plan');
  }
  
  return response.json();
};