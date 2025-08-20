export const STORAGE_KEYS = {
  CHARGE_RATE: 'ev_charging_charge_rate',
  ENERGY_NEEDED: 'ev_charging_energy_needed',
  DEADLINE: 'ev_charging_deadline',
  MODE: 'ev_charging_mode',
} as const;

export const getStoredValue = (key: string, defaultValue: string): string => {
  if (typeof window === 'undefined') return defaultValue;
  return localStorage.getItem(key) ?? defaultValue;
};

export const setStoredValue = (key: string, value: string): void => {
  if (typeof window === 'undefined') return;
  localStorage.setItem(key, value);
};