'use client';

import { useQuery } from '@tanstack/react-query';
import { ComposedChart, Line, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceDot } from 'recharts';
import { fetchPrices } from '@/api/client';
import { format, parseISO } from 'date-fns';

interface PriceChartProps {
  date: string;
  selectedHours: string[];
}

export default function PriceChart({ date, selectedHours }: PriceChartProps) {
  const { data: prices, isLoading, error } = useQuery({
    queryKey: ['prices', date],
    queryFn: () => fetchPrices(date),
  });

  if (isLoading) {
    return (
      <div className="h-64 flex items-center justify-center">
        <div className="text-gray-500">Loading prices...</div>
      </div>
    );
  }

  if (error) {
    const errorMessage = error instanceof Error ? error.message : 'Error loading prices';
    return (
      <div className="h-64 flex items-center justify-center">
        <div className="text-center">
          <div className="text-red-500 font-medium mb-2">Error loading prices</div>
          <div className="text-sm text-gray-600 max-w-md">
            {errorMessage.includes('Historical data') || errorMessage.includes('Future data') 
              ? errorMessage 
              : 'Please check your connection and try again.'}
          </div>
        </div>
      </div>
    );
  }

  const chartData = prices?.map(price => {
    const isSelected = selectedHours.includes(price.hour);
    const priceValue = Number(price.pricePerKWh.toFixed(3));
    return {
      hour: format(parseISO(price.hour), 'HH:mm'),
      price: priceValue,
      chargingBar: isSelected ? priceValue : 0,
      timestamp: price.hour,
      isSelected
    };
  }) || [];

  const now = new Date();
  const currentHour = format(now, 'HH:mm');

  const CustomTooltip = ({ active, payload, label }: any) => {
    if (active && payload && payload.length) {
      const pricePayload = payload.find((p: any) => p.dataKey === 'price');
      const chargingPayload = payload.find((p: any) => p.dataKey === 'chargingBar');
      const isCharging = chargingPayload && chargingPayload.value > 0;
      
      return (
        <div className="bg-white p-3 border border-gray-200 rounded shadow-lg">
          <p className="font-medium">{`Time: ${label}`}</p>
          <p className="text-blue-600">
            {`Price: €${pricePayload?.value}/kWh`}
          </p>
          {isCharging && (
            <p className="text-green-600 font-medium">
              ⚡ Charging Hour
            </p>
          )}
        </div>
      );
    }
    return null;
  };

  return (
    <div className="h-64">
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis 
            dataKey="hour" 
            tick={{ fontSize: 12 }}
            interval="preserveStartEnd"
          />
          <YAxis 
            tick={{ fontSize: 12 }}
            tickFormatter={(value) => `€${value}`}
          />
          <Tooltip content={<CustomTooltip />} />
          <Bar
            dataKey="chargingBar"
            fill="#10b981"
            fillOpacity={0.3}
            stroke="#10b981"
            strokeWidth={1}
          />
          <Line
            type="monotone"
            dataKey="price"
            stroke="#2563eb"
            strokeWidth={2}
            dot={{ fill: '#2563eb', r: 3 }}
          />
          {chartData.map((point, index) => {
            if (point.hour === currentHour) {
              return (
                <ReferenceDot
                  key={`current-${index}`}
                  x={point.hour}
                  y={point.price}
                  r={6}
                  fill="#ef4444"
                  stroke="#ffffff"
                  strokeWidth={2}
                />
              );
            }
            return null;
          })}
        </ComposedChart>
      </ResponsiveContainer>
      <div className="mt-2 flex justify-center space-x-4 text-sm">
        <div className="flex items-center">
          <div className="w-3 h-3 bg-red-500 rounded-full mr-1"></div>
          <span>Current Hour</span>
        </div>
        <div className="flex items-center">
          <div className="w-3 h-3 bg-green-500 opacity-60 mr-1"></div>
          <span>Charging Hours</span>
        </div>
      </div>
    </div>
  );
}