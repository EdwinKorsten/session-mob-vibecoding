package com.evcharging.dynamicpricing.service;

import com.evcharging.dynamicpricing.config.AppConfig;
import com.evcharging.dynamicpricing.domain.PricePoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class EnergyChartsClient {

    private final RestTemplate restTemplate;
    private final AppConfig appConfig;
    private final ObjectMapper objectMapper;

    public EnergyChartsClient(RestTemplate restTemplate, AppConfig appConfig) {
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
        this.objectMapper = new ObjectMapper();
    }

    @Cacheable("prices")
    public List<PricePoint> fetchPrices(LocalDate date) {
        // Check if the requested date is supported
        LocalDate today = LocalDate.now(ZoneId.of(appConfig.getTimezone()));
        LocalDate tomorrow = today.plusDays(1);
        
        if (date.isBefore(today)) {
            throw new IllegalArgumentException("Historical data is not available. Energy Charts API only provides data for today and tomorrow.");
        }
        
        if (date.isAfter(tomorrow)) {
            throw new IllegalArgumentException("Future data beyond tomorrow is not available. Energy Charts API only provides data for today and tomorrow.");
        }
        
        String url = String.format("%s/price?bzn=%s", 
            appConfig.getPricesApiBase(), 
            appConfig.getBzn());
        
        try {
            EnergyChartsResponse response = restTemplate.getForObject(url, EnergyChartsResponse.class);
            
            if (response == null || response.unix_seconds == null || response.price == null) {
                throw new RuntimeException("Invalid response from Energy Charts API");
            }

            List<PricePoint> filteredPrices = IntStream.range(0, Math.min(response.unix_seconds.length, response.price.length))
                .mapToObj(i -> {
                    long timestamp = response.unix_seconds[i];
                    double priceEurPerMWh = response.price[i];
                    
                    ZonedDateTime hour = Instant.ofEpochSecond(timestamp)
                        .atZone(ZoneId.of(appConfig.getTimezone()));
                    
                    BigDecimal pricePerKWh = BigDecimal.valueOf(priceEurPerMWh)
                        .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
                    
                    return new PricePoint(hour, pricePerKWh);
                })
                .filter(pricePoint -> pricePoint.hour().toLocalDate().equals(date))
                .toList();
            
            if (filteredPrices.isEmpty()) {
                if (date.equals(tomorrow)) {
                    throw new RuntimeException("Tomorrow's prices are not yet available. They are usually published after 14:00 CET.");
                } else {
                    throw new RuntimeException("No price data available for the requested date.");
                }
            }
            
            return filteredPrices;
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw validation exceptions as-is
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch prices from Energy Charts API: " + e.getMessage(), e);
        }
    }

    private static class EnergyChartsResponse {
        @JsonProperty("unix_seconds")
        public long[] unix_seconds;
        
        @JsonProperty("price")
        public double[] price;
    }
}