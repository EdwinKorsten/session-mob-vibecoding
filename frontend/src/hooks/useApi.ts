'use client';

import { useQuery, useMutation } from '@tanstack/react-query';
import { PricePoint, ChargingPlanRequest, ChargingPlan } from '@/types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api';

export function usePrices(date?: string) {
  const dateParam = date || new Date().toISOString().split('T')[0];
  
  return useQuery({
    queryKey: ['prices', dateParam],
    queryFn: async (): Promise<PricePoint[]> => {
      const response = await fetch(`${API_BASE_URL}/prices?date=${dateParam}`);
      if (!response.ok) {
        throw new Error('Failed to fetch prices');
      }
      return response.json();
    },
    staleTime: 15 * 60 * 1000, // 15 minutes
    refetchOnWindowFocus: false,
  });
}

export function useChargingPlanMutation() {
  return useMutation({
    mutationFn: async (request: ChargingPlanRequest): Promise<ChargingPlan> => {
      const response = await fetch(`${API_BASE_URL}/plan`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(request),
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to calculate charging plan: ${errorText}`);
      }
      
      return response.json();
    },
  });
}