# EV Charging Optimizer

A production-ready web application that fetches day-ahead hourly electricity prices and computes the cheapest time(s) to charge an Electric Vehicle before a deadline.

## Features

- **Hourly Price Visualization**: Interactive line chart showing electricity prices throughout the day
- **Charging Plan Calculator**: Calculate optimal charging times with two modes:
  - **Continuous**: Find the best consecutive hours for charging
  - **Discrete**: Find the cheapest individual hours (not necessarily consecutive)
- **Real-time Optimization**: Dynamic calculation based on energy needs, charging rate, and deadline
- **Multi-region Support**: Configurable bidding zones (DE-LU, NL, FR, etc.)
- **Responsive Design**: Works on desktop and mobile devices
- **Persistent Settings**: User preferences saved in browser localStorage

## Architecture

### Backend (Java Spring Boot 3)
- **REST API** with endpoints for prices and charging plans
- **Energy Charts Integration** for fetching real-time electricity prices
- **Caching** via Caffeine for improved performance
- **Validation** and error handling with detailed messages
- **Time Zone Support** for accurate price calculations

### Frontend (Next.js + React + TypeScript)
- **Server-side Rendering** with App Router
- **React Query** for efficient data fetching and caching  
- **Recharts** for interactive price visualization
- **Tailwind CSS** for responsive styling
- **TypeScript** for type safety

## Quick Start

### Using Docker Compose (Recommended)

1. **Clone and setup**:
   ```bash
   git clone <repository-url>
   cd ev-charging-optimizer
   cp .env.example .env
   ```

2. **Configure region** (optional):
   Edit `.env` to change the bidding zone:
   ```bash
   # For Netherlands
   BZN=NL
   
   # For France  
   BZN=FR
   
   # For Germany/Luxembourg (default)
   BZN=DE-LU
   ```

3. **Start the application**:
   ```bash
   docker-compose up --build
   ```

4. **Access the application**:
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api

### Development Setup

#### Backend
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `BZN` | Energy Charts bidding zone | `DE-LU` |
| `TIMEZONE` | Timezone for calculations | `Europe/Amsterdam` |
| `PRICES_API_BASE` | Energy Charts API URL | `https://api.energy-charts.info` |
| `NEXT_PUBLIC_API_BASE_URL` | Frontend API endpoint | `http://localhost:8080/api` |

### Supported Bidding Zones

- `DE-LU`: Germany/Luxembourg
- `NL`: Netherlands  
- `FR`: France
- `DK-1`: Denmark West
- `DK-2`: Denmark East
- `SE-1` to `SE-4`: Sweden zones
- `NO-1` to `NO-5`: Norway zones

## API Documentation

### GET `/api/prices`
Fetch hourly electricity prices for a specific date.

**Parameters:**
- `date` (optional): Date in YYYY-MM-DD format, defaults to today

**Response:**
```json
[
  {
    "hour": "2025-08-20T06:00:00+02:00",
    "pricePerKWh": 0.154
  }
]
```

### POST `/api/plan`
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

## Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests  
```bash
cd frontend
npm test
```

## Algorithms

### Continuous Mode
Uses a sliding window approach to find the cheapest consecutive hours:
1. Calculate required hours: `ceil(energyNeeded / chargeRate)`
2. Filter eligible hours (before deadline)
3. Slide window of required width, sum costs
4. Return window with minimum total cost

### Discrete Mode
Selects the cheapest individual hours regardless of sequence:
1. Calculate required hours: `ceil(energyNeeded / chargeRate)`
2. Filter eligible hours (before deadline)  
3. Sort by price (ascending), then by time for ties
4. Select the N cheapest hours

## Error Handling

The application provides meaningful error messages for:
- **Insufficient Time**: Not enough hours before deadline
- **API Failures**: Energy Charts API unavailable
- **Validation Errors**: Invalid input parameters
- **Network Issues**: Connection problems

## Performance Features

- **Caching**: 15-minute cache for price data
- **Lazy Loading**: Components load only when needed
- **Optimistic Updates**: UI responds immediately to user actions
- **Error Boundaries**: Graceful handling of component failures

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes with tests
4. Submit a pull request

## License

MIT License - see LICENSE file for details.

## Support

For issues and questions:
- Create an issue in the repository
- Check the API status at https://api.energy-charts.info

---

**Powered by [Energy Charts API](https://api.energy-charts.info)** | **Built with Spring Boot & Next.js**
