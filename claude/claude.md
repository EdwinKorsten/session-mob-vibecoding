Dynamic Power Prices EV Charging Web App (Energy-Charts API)
Goal

Create a production-ready web application that fetches day-ahead hourly electricity prices and computes the cheapest time(s) to charge an EV before a deadline.

Tech Stack (required)

Backend: Java Spring Boot 3

Frontend: Next.js (App Router) + React + TypeScript

Charting: Recharts (or similar)

Containerization: Dockerfiles for FE/BE + docker-compose.yml

External Data (fixed)

Use Energy-Charts day-ahead price endpoint:
https://api.energy-charts.info/price?bzn=${BZN}

Env var: BZN (default: DE-LU; allow overrides like NL, FR, etc.)

Returned prices are EUR/MWh → convert to EUR/kWh by dividing by 1000.

Treat timestamps from API as ISO/epoch provided; normalize to Europe/Amsterdam for display and calculations.

Core Features

Hourly Price Graph

Line chart of 00:00–23:00 for the selected date.

Tooltip shows time and €/kWh (3 decimals).

Highlight current hour.

Day Filter

Date picker (default today).

On change → re-fetch /api/prices?date=YYYY-MM-DD.

Charging Plan Calculator

Inputs with defaults & validation:

Finish by: time input, default 14:00 (same day).

Charge rate (kWh/h): default 10.

Energy needed (kWh): default 80.

Mode toggle: Continuous (default) vs Discrete (cheapest hours not necessarily consecutive).

Algorithm & Output:

Let H = ceil(energyNeeded / chargeRate).

Eligible hours end no later than the deadline.

Continuous: slide a window of width H; choose min total cost → return start time.

Discrete: pick the H cheapest eligible hours; break ties by earliest hour.

Show total cost (€) and average €/kWh, and overlay chosen hours on the chart.

Error when infeasible (not enough hours); suggest earliest feasible deadline.

Backend (Spring Boot 3)

Packages

domain: PricePoint { ZonedDateTime hour; BigDecimal pricePerKWh; }

service:

EnergyChartsClient (fetch & map API → List<PricePoint>; caching via Caffeine)

ChargingPlanService (continuous/discrete planners)

web: REST controllers + DTOs + Bean Validation

Config

BZN (default DE-LU), PRICES_API_BASE=https://api.energy-charts.info, time zone Europe/Amsterdam.

Endpoints

GET /api/prices?date=YYYY-MM-DD → [ { "hour":"2025-08-20T06:00:00+02:00", "pricePerKWh":0.154 } ]

POST /api/plan (request)

{
"date": "YYYY-MM-DD",
"deadline": "HH:mm",
"timezone": "Europe/Amsterdam",
"chargeRateKwhPerHour": 10,
"energyNeededKwh": 80,
"continuous": true
}


(response)

{
"mode": "CONTINUOUS",
"requiredHours": 8,
"startTime": "2025-08-20T06:00:00+02:00",
"selectedHours": ["2025-08-20T06:00:00+02:00", "..."],
"totalCostEur": 12.34,
"avgPriceEurPerKwh": 0.154,
"deadline": "2025-08-20T14:00:00+02:00"
}


Notes

Convert EUR/MWh → EUR/kWh (/1000).

Normalize all timestamps to Europe/Amsterdam.

Handle API/network errors; return readable messages.

Unit tests for planner edge cases (DST changes, missing hours, infeasible plan).

Frontend (Next.js + React)

Route: / (dashboard)

Components: PriceChart, DatePicker, ChargingForm, PlanSummary

Data fetching: React Query (SWR ok) to call /api/prices and /api/plan

UX:

Default date = today; default finish-by = 14:00.

Persist last-used inputs in localStorage.

Loading/empty/error states; accessible labels; 24h times.

Overlay recommended hours on chart.

DevOps

.env (FE/BE) documenting BZN and timezone.

Dockerfiles + docker-compose up to run full stack.

README with setup and how to switch BZN (e.g., DE-LU for Germany, NL for Netherlands).

Definition of Done

Users can select a date (default today) and see hourly €/kWh prices.

Clicking Calculate returns a start time (continuous) or chosen hours (discrete), with total cost and chart overlay.

Works against Energy-Charts API with configurable BZN.

Robust handling of missing/incomplete data; all times in Europe/Amsterdam.