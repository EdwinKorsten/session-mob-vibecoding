interface DatePickerProps {
  selectedDate: Date;
  onDateChange: (date: Date) => void;
}

export default function DatePicker({ selectedDate, onDateChange }: DatePickerProps) {
  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onDateChange(new Date(e.target.value));
  };

  const formatDateForInput = (date: Date) => {
    return date.toISOString().split('T')[0];
  };

  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);

  const minDate = formatDateForInput(today);
  const maxDate = formatDateForInput(tomorrow);

  return (
    <div className="space-y-2">
      <div className="flex items-center space-x-4">
        <label htmlFor="date" className="text-sm font-medium text-gray-700">
          Select Date:
        </label>
        <input
          type="date"
          id="date"
          value={formatDateForInput(selectedDate)}
          onChange={handleDateChange}
          min={minDate}
          max={maxDate}
          className="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
      </div>
      <p className="text-xs text-gray-500">
        * Data is only available for today and tomorrow. Tomorrow's prices are usually available after 14:00 CET.
      </p>
    </div>
  );
}