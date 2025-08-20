'use client';

import { useState } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import Dashboard from '@/components/Dashboard';

const queryClient = new QueryClient();

export default function Home() {
  return (
    <QueryClientProvider client={queryClient}>
      <main className="min-h-screen bg-gray-50 py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-gray-900">
              Dynamic EV Charging Planner
            </h1>
            <p className="mt-2 text-gray-600">
              Find the cheapest time to charge your electric vehicle
            </p>
          </div>
          <Dashboard />
        </div>
      </main>
    </QueryClientProvider>
  );
}