package com.evcharging.service;

import com.evcharging.domain.PricePoint;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EnergyChartsClient {
    private final WebClient webClient;
    private final String bzn;
    private final ZoneId timezone;
    private final ObjectMapper objectMapper;

    public EnergyChartsClient(WebClient.Builder webClientBuilder,
                             @Value("${app.energy-charts.base-url:https://api.energy-charts.info}") String baseUrl,
                             @Value("${app.energy-charts.bzn:DE-LU}") String bzn,
                             @Value("${app.timezone:Europe/Amsterdam}") String timezone,
                             ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.bzn = bzn;
        this.timezone = ZoneId.of(timezone);
        this.objectMapper = objectMapper;
    }

    @Cacheable("prices")
    public List<PricePoint> fetchPrices(LocalDate date) {
        try {
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/price")
                            .queryParam("bzn", bzn)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            EnergyChartsResponse energyResponse = objectMapper.readValue(response, EnergyChartsResponse.class);
            return convertToPricePoints(energyResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch prices from Energy Charts API", e);
        }
    }

    private List<PricePoint> convertToPricePoints(EnergyChartsResponse response) {
        List<PricePoint> pricePoints = new ArrayList<>();
        
        if (response.getPrice() != null && response.getUnixSeconds() != null) {
            for (int i = 0; i < Math.min(response.getPrice().size(), response.getUnixSeconds().size()); i++) {
                Long timestamp = response.getUnixSeconds().get(i);
                Double priceEurMwh = response.getPrice().get(i);
                
                if (timestamp != null && priceEurMwh != null) {
                    ZonedDateTime hour = Instant.ofEpochSecond(timestamp)
                            .atZone(timezone);
                    
                    BigDecimal priceEurKwh = BigDecimal.valueOf(priceEurMwh)
                            .divide(BigDecimal.valueOf(1000), 6, RoundingMode.HALF_UP);
                    
                    pricePoints.add(new PricePoint(hour, priceEurKwh));
                }
            }
        }
        
        return pricePoints;
    }

    public static class EnergyChartsResponse {
        @JsonProperty("price")
        private List<Double> price;
        
        @JsonProperty("unix_seconds")
        private List<Long> unixSeconds;

        public List<Double> getPrice() {
            return price;
        }

        public void setPrice(List<Double> price) {
            this.price = price;
        }

        public List<Long> getUnixSeconds() {
            return unixSeconds;
        }

        public void setUnixSeconds(List<Long> unixSeconds) {
            this.unixSeconds = unixSeconds;
        }
    }
}