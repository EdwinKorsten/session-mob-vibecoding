export interface PricePoint {
  hour: string;
  pricePerKWh: number;
}

export interface ChargingPlanRequest {
  date: string;
  deadline: string;
  timezone: string;
  chargeRateKwhPerHour: number;
  energyNeededKwh: number;
  continuous: boolean;
}

export interface ChargingPlan {
  mode: 'CONTINUOUS' | 'DISCRETE';
  requiredHours: number;
  startTime: string;
  selectedHours: string[];
  totalCostEur: number;
  avgPriceEurPerKwh: number;
  deadline: string;
}