# Dynamic Power Prices EV Charging Web App

A production-ready web application that fetches day-ahead hourly electricity prices from Energy-Charts API and computes the cheapest time(s) to charge an electric vehicle before a deadline.

## Features

- **Hourly Price Graph**: Interactive line chart showing electricity prices for the selected date
- **Day Filter**: Date picker to view prices for different days
- **Charging Plan Calculator**: 
  - Configure charging parameters (deadline, charge rate, energy needed)
  - Choose between continuous and discrete charging modes
  - Get optimal charging schedule with cost breakdown
- **Real-time Updates**: Chart highlights current hour and selected charging hours

## Tech Stack

- **Backend**: Java Spring Boot 3 with REST API
- **Frontend**: Next.js (App Router) + React + TypeScript
- **Charting**: Recharts
- **Containerization**: Docker & Docker Compose
- **External API**: Energy-Charts day-ahead price endpoint

## Quick Start

### Using Docker Compose (Recommended)

1. Clone the repository:
```bash
git clone <repository-url>
cd dynamic-pricing-ev-charging
```

2. Start the application:
```bash
docker-compose up --build
```

3. Access the application:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

### Manual Setup

#### Backend (Spring Boot)

1. Navigate to backend directory:
```bash
cd backend
```

2. Run the application:
```bash
./mvnw spring-boot:run
```

The backend will start on port 8080.

#### Frontend (Next.js)

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will start on port 3000.

## Configuration

### Environment Variables

Copy `.env.example` to `.env` and configure as needed:

```bash
cp .env.example .env
```

#### Backend Configuration

- `BZN`: Bidding zone (default: DE-LU)
  - `DE-LU`: Germany/Luxembourg
  - `NL`: Netherlands  
  - `FR`: France
  - `AT`: Austria
  - `BE`: Belgium
  - `CH`: Switzerland
  - `CZ`: Czech Republic

- `PRICES_API_BASE`: Energy Charts API base URL (default: https://api.energy-charts.info)
- `TIMEZONE`: Application timezone (default: Europe/Amsterdam)

#### Frontend Configuration

- `NEXT_PUBLIC_API_URL`: Backend API URL (default: http://localhost:8080/api)

### Switching Between Countries

To switch to a different country's electricity prices, update the `BZN` environment variable:

```bash
# For Netherlands
BZN=NL

# For France  
BZN=FR

# For Austria
BZN=AT
```

Then restart the application:
```bash
docker-compose down
docker-compose up --build
```

## API Endpoints

### GET /api/prices
Fetch hourly electricity prices for a specific date.

**Parameters:**
- `date`: Date in YYYY-MM-DD format

**Response:**
```json
[
  {
    "hour": "2025-08-20T06:00:00+02:00",
    "pricePerKWh": 0.154
  }
]
```

### POST /api/plan
Calculate optimal charging plan.

**Request:**
```json
{
  "date": "2025-08-20",
  "deadline": "14:00",
  "timezone": "Europe/Amsterdam",
  "chargeRateKwhPerHour": 10,
  "energyNeededKwh": 80,
  "continuous": true
}
```

**Response:**
```json
{
  "mode": "CONTINUOUS",
  "requiredHours": 8,
  "startTime": "2025-08-20T06:00:00+02:00",
  "selectedHours": ["2025-08-20T06:00:00+02:00", "..."],
  "totalCostEur": 12.34,
  "avgPriceEurPerKwh": 0.154,
  "deadline": "2025-08-20T14:00:00+02:00"
}
```

## Charging Modes

### Continuous Mode
Finds the optimal consecutive time window for charging. Best for scenarios where you need uninterrupted charging.

### Discrete Mode  
Selects the cheapest individual hours regardless of continuity. Maximizes cost savings but may require starting/stopping the charger multiple times.

## Development

### Running Tests

Backend:
```bash
cd backend
./mvnw test
```

Frontend:
```bash
cd frontend
npm test
```

### Building for Production

Backend:
```bash
cd backend
./mvnw clean package
```

Frontend:
```bash
cd frontend
npm run build
```

## Troubleshooting

### Common Issues

1. **API Connection Error**: Ensure the Energy Charts API is accessible and the BZN code is valid.

2. **CORS Issues**: The backend is configured to allow all origins. In production, update CORS settings.

3. **Container Build Issues**: Ensure Docker and Docker Compose are installed and running.

4. **Port Conflicts**: If ports 3000 or 8080 are in use, update the port mappings in docker-compose.yml.

### Logs

View application logs:
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs backend
docker-compose logs frontend
```

## License

MIT License