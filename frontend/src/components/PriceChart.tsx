'use client';

import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, ReferenceDot } from 'recharts';
import { format, parseISO } from 'date-fns';
import { PricePoint, ChargingPlan } from '@/types';

interface PriceChartProps {
  data: PricePoint[];
  plan: ChargingPlan | null;
  isLoading?: boolean;
}

interface ChartDataPoint {
  time: string;
  price: number;
  hour: string;
  isSelected: boolean;
  isCurrent: boolean;
}

export default function PriceChart({ data, plan, isLoading }: PriceChartProps) {
  if (isLoading) {
    return (
      <div className="bg-white p-6 rounded-lg shadow">
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="h-64 bg-gray-200 rounded"></div>
        </div>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="bg-white p-6 rounded-lg shadow">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Hourly Electricity Prices</h2>
        <div className="text-center text-gray-500 py-8">
          No price data available
        </div>
      </div>
    );
  }

  const currentHour = new Date().getHours();
  const selectedHourStrings = plan?.selectedHours || [];
  
  const chartData: ChartDataPoint[] = data.map(point => {
    const hour = parseISO(point.hour);
    const timeString = format(hour, 'HH:mm');
    const hourNum = hour.getHours();
    
    return {
      time: timeString,
      price: Math.round(point.pricePerKWh * 1000) / 1000,
      hour: point.hour,
      isSelected: selectedHourStrings.includes(point.hour),
      isCurrent: hourNum === currentHour,
    };
  });

  const CustomTooltip = ({ active, payload, label }: { active?: boolean; payload?: Array<{ payload: { hour: string; price: number; isSelected: boolean; isCurrent: boolean } }>; label?: string }) => {
    if (active && payload && payload.length) {
      const data = payload[0].payload;
      return (
        <div className="bg-white p-3 border border-gray-200 rounded shadow-lg">
          <p className="font-medium">{`Time: ${label}`}</p>
          <p className="text-blue-600">
            {`Price: €${data.price.toFixed(3)}/kWh`}
          </p>
          {data.isSelected && (
            <p className="text-green-600 text-sm">Selected for charging</p>
          )}
          {data.isCurrent && (
            <p className="text-orange-600 text-sm">Current hour</p>
          )}
        </div>
      );
    }
    return null;
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow">
      <h2 className="text-xl font-semibold text-gray-900 mb-4">
        Hourly Electricity Prices (€/kWh)
      </h2>
      
      <div className="h-64 md:h-80">
        <ResponsiveContainer width="100%" height="100%">
          <LineChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis 
              dataKey="time" 
              stroke="#666"
              fontSize={12}
              interval="preserveStartEnd"
            />
            <YAxis 
              stroke="#666"
              fontSize={12}
              tickFormatter={(value) => `€${value.toFixed(3)}`}
            />
            <Tooltip content={<CustomTooltip />} />
            <Line 
              type="monotone" 
              dataKey="price" 
              stroke="#2563eb" 
              strokeWidth={2}
              dot={{ fill: '#2563eb', strokeWidth: 2, r: 3 }}
              activeDot={{ r: 5, stroke: '#2563eb', strokeWidth: 2 }}
            />
            
            {chartData.map((point, index) => {
              if (point.isCurrent) {
                return (
                  <ReferenceDot 
                    key={`current-${index}`}
                    x={point.time} 
                    y={point.price} 
                    r={6} 
                    fill="#f97316" 
                    stroke="#ffffff"
                    strokeWidth={2}
                  />
                );
              }
              if (point.isSelected) {
                return (
                  <ReferenceDot 
                    key={`selected-${index}`}
                    x={point.time} 
                    y={point.price} 
                    r={5} 
                    fill="#10b981" 
                    stroke="#ffffff"
                    strokeWidth={2}
                  />
                );
              }
              return null;
            })}
          </LineChart>
        </ResponsiveContainer>
      </div>
      
      <div className="flex flex-wrap gap-4 mt-4 text-sm text-gray-900 chart-legend">
        <div className="flex items-center">
          <div className="w-3 h-3 bg-blue-600 rounded-full mr-2"></div>
          <span style={{ color: '#000000' }}>Hourly prices</span>
        </div>
        <div className="flex items-center">
          <div className="w-3 h-3 bg-orange-500 rounded-full mr-2"></div>
          <span style={{ color: '#000000' }}>Current hour</span>
        </div>
        {plan && (
          <div className="flex items-center">
            <div className="w-3 h-3 bg-green-500 rounded-full mr-2"></div>
            <span style={{ color: '#000000' }}>Selected for charging</span>
          </div>
        )}
      </div>
    </div>
  );
}