package com.evcharging.dynamicpricing.service;

import com.evcharging.dynamicpricing.domain.PricePoint;
import com.evcharging.dynamicpricing.web.dto.ChargingPlanRequest;
import com.evcharging.dynamicpricing.web.dto.ChargingPlanResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ChargingPlanService {

    private final EnergyChartsClient energyChartsClient;

    public ChargingPlanService(EnergyChartsClient energyChartsClient) {
        this.energyChartsClient = energyChartsClient;
    }

    public ChargingPlanResponse calculatePlan(ChargingPlanRequest request) {
        List<PricePoint> prices = energyChartsClient.fetchPrices(request.date());
        
        int requiredHours = (int) Math.ceil(request.energyNeededKwh() / request.chargeRateKwhPerHour());
        
        ZonedDateTime deadline = ZonedDateTime.of(
            request.date(), 
            request.deadline(), 
            ZoneId.of(request.timezone())
        );
        
        List<PricePoint> eligibleHours = prices.stream()
            .filter(price -> price.hour().isBefore(deadline) || price.hour().equals(deadline.minusHours(1)))
            .toList();
        
        if (eligibleHours.size() < requiredHours) {
            ZonedDateTime earliestFeasible = prices.get(Math.max(0, prices.size() - requiredHours)).hour().plusHours(requiredHours);
            throw new IllegalArgumentException(
                String.format("Not enough hours available before deadline. Earliest feasible deadline: %s", 
                    earliestFeasible.toLocalTime())
            );
        }

        if (request.continuous()) {
            return calculateContinuousPlan(eligibleHours, requiredHours, request);
        } else {
            return calculateDiscretePlan(eligibleHours, requiredHours, request);
        }
    }

    private ChargingPlanResponse calculateContinuousPlan(List<PricePoint> eligibleHours, int requiredHours, ChargingPlanRequest request) {
        BigDecimal minCost = BigDecimal.valueOf(Double.MAX_VALUE);
        int bestStartIndex = 0;
        
        for (int i = 0; i <= eligibleHours.size() - requiredHours; i++) {
            BigDecimal cost = IntStream.range(i, i + requiredHours)
                .mapToObj(eligibleHours::get)
                .map(PricePoint::pricePerKWh)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (cost.compareTo(minCost) < 0) {
                minCost = cost;
                bestStartIndex = i;
            }
        }
        
        List<ZonedDateTime> selectedHours = IntStream.range(bestStartIndex, bestStartIndex + requiredHours)
            .mapToObj(i -> eligibleHours.get(i).hour())
            .toList();
        
        BigDecimal totalCost = minCost.multiply(BigDecimal.valueOf(request.chargeRateKwhPerHour()))
            .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal avgPrice = minCost.divide(BigDecimal.valueOf(requiredHours), 6, RoundingMode.HALF_UP);
        
        ZonedDateTime deadline = ZonedDateTime.of(
            request.date(), 
            request.deadline(), 
            ZoneId.of(request.timezone())
        );
        
        return new ChargingPlanResponse(
            ChargingPlanResponse.Mode.CONTINUOUS,
            requiredHours,
            selectedHours.get(0),
            selectedHours,
            totalCost,
            avgPrice,
            deadline
        );
    }

    private ChargingPlanResponse calculateDiscretePlan(List<PricePoint> eligibleHours, int requiredHours, ChargingPlanRequest request) {
        List<PricePoint> sortedByPrice = eligibleHours.stream()
            .sorted(Comparator.comparing(PricePoint::pricePerKWh)
                .thenComparing(PricePoint::hour))
            .limit(requiredHours)
            .toList();
        
        List<ZonedDateTime> selectedHours = sortedByPrice.stream()
            .map(PricePoint::hour)
            .sorted()
            .toList();
        
        BigDecimal totalPricePerKWh = sortedByPrice.stream()
            .map(PricePoint::pricePerKWh)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCost = totalPricePerKWh.multiply(BigDecimal.valueOf(request.chargeRateKwhPerHour()))
            .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal avgPrice = totalPricePerKWh.divide(BigDecimal.valueOf(requiredHours), 6, RoundingMode.HALF_UP);
        
        ZonedDateTime deadline = ZonedDateTime.of(
            request.date(), 
            request.deadline(), 
            ZoneId.of(request.timezone())
        );
        
        return new ChargingPlanResponse(
            ChargingPlanResponse.Mode.DISCRETE,
            requiredHours,
            selectedHours.get(0),
            selectedHours,
            totalCost,
            avgPrice,
            deadline
        );
    }
}