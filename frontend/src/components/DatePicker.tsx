'use client';

import { format } from 'date-fns';

interface DatePickerProps {
  value: Date;
  onChange: (date: Date) => void;
}

export default function DatePicker({ value, onChange }: DatePickerProps) {
  const handleDateChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newDate = new Date(event.target.value);
    onChange(newDate);
  };

  return (
    <div className="flex flex-col space-y-2">
      <label htmlFor="date-picker" className="text-sm font-medium text-gray-900">
        Date
      </label>
      <div className="relative">
        <input
          id="date-picker"
          type="date"
          value={format(value, 'yyyy-MM-dd')}
          onChange={handleDateChange}
          className="w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
          style={{ color: '#000000' }}
          aria-label="Select date for price data"
        />
      </div>
    </div>
  );
}