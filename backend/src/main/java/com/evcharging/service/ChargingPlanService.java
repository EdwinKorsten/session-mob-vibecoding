package com.evcharging.service;

import com.evcharging.domain.ChargingPlan;
import com.evcharging.domain.ChargingPlanRequest;
import com.evcharging.domain.PricePoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChargingPlanService {
    private final EnergyChartsClient energyChartsClient;
    private final ZoneId timezone;

    public ChargingPlanService(EnergyChartsClient energyChartsClient,
                              @Value("${app.timezone:Europe/Amsterdam}") String timezone) {
        this.energyChartsClient = energyChartsClient;
        this.timezone = ZoneId.of(timezone);
    }

    public ChargingPlan calculateOptimalChargingPlan(ChargingPlanRequest request) {
        List<PricePoint> pricePoints = energyChartsClient.fetchPrices(request.getDate());
        
        int requiredHours = calculateRequiredHours(request.getEnergyNeededKwh(), 
                                                  request.getChargeRateKwhPerHour());
        
        ZonedDateTime deadline = request.getDate().atTime(request.getDeadline())
                .atZone(ZoneId.of(request.getTimezone()));
        
        List<PricePoint> eligibleHours = filterEligibleHours(pricePoints, deadline);
        
        if (eligibleHours.size() < requiredHours) {
            throw new IllegalArgumentException(
                String.format("Not enough hours available before deadline. Need %d hours, but only %d available. " +
                            "Consider setting deadline to %s or later.", 
                            requiredHours, eligibleHours.size(), 
                            calculateEarliestFeasibleDeadline(pricePoints, requiredHours)));
        }

        if (request.isContinuous()) {
            return calculateContinuousPlan(eligibleHours, requiredHours, deadline, request.getEnergyNeededKwh());
        } else {
            return calculateDiscretePlan(eligibleHours, requiredHours, deadline, request.getEnergyNeededKwh());
        }
    }

    private int calculateRequiredHours(BigDecimal energyNeeded, BigDecimal chargeRate) {
        return energyNeeded.divide(chargeRate, 0, RoundingMode.CEILING).intValue();
    }

    private List<PricePoint> filterEligibleHours(List<PricePoint> pricePoints, ZonedDateTime deadline) {
        return pricePoints.stream()
                .filter(point -> point.getHour().isBefore(deadline) || point.getHour().equals(deadline))
                .sorted(Comparator.comparing(PricePoint::getHour))
                .collect(Collectors.toList());
    }

    private ChargingPlan calculateContinuousPlan(List<PricePoint> eligibleHours, int requiredHours, 
                                               ZonedDateTime deadline, BigDecimal energyNeeded) {
        BigDecimal minTotalCost = null;
        int bestStartIndex = -1;
        
        for (int i = 0; i <= eligibleHours.size() - requiredHours; i++) {
            BigDecimal windowCost = BigDecimal.ZERO;
            for (int j = i; j < i + requiredHours; j++) {
                windowCost = windowCost.add(eligibleHours.get(j).getPricePerKWh());
            }
            
            if (minTotalCost == null || windowCost.compareTo(minTotalCost) < 0) {
                minTotalCost = windowCost;
                bestStartIndex = i;
            }
        }
        
        List<ZonedDateTime> selectedHours = new ArrayList<>();
        ZonedDateTime startTime = eligibleHours.get(bestStartIndex).getHour();
        
        for (int i = bestStartIndex; i < bestStartIndex + requiredHours; i++) {
            selectedHours.add(eligibleHours.get(i).getHour());
        }
        
        BigDecimal totalCost = minTotalCost.multiply(energyNeeded)
                .divide(BigDecimal.valueOf(requiredHours), 2, RoundingMode.HALF_UP);
        BigDecimal avgPrice = minTotalCost.divide(BigDecimal.valueOf(requiredHours), 6, RoundingMode.HALF_UP);
        
        return new ChargingPlan(
                ChargingPlan.ChargingMode.CONTINUOUS,
                requiredHours,
                startTime,
                selectedHours,
                totalCost,
                avgPrice,
                deadline
        );
    }

    private ChargingPlan calculateDiscretePlan(List<PricePoint> eligibleHours, int requiredHours, 
                                             ZonedDateTime deadline, BigDecimal energyNeeded) {
        List<PricePoint> sortedByPrice = eligibleHours.stream()
                .sorted(Comparator.comparing(PricePoint::getPricePerKWh)
                        .thenComparing(PricePoint::getHour))
                .collect(Collectors.toList());
        
        List<PricePoint> selectedPoints = sortedByPrice.subList(0, requiredHours);
        List<ZonedDateTime> selectedHours = selectedPoints.stream()
                .map(PricePoint::getHour)
                .sorted()
                .collect(Collectors.toList());
        
        ZonedDateTime startTime = selectedHours.isEmpty() ? null : selectedHours.get(0);
        
        BigDecimal totalPriceSum = selectedPoints.stream()
                .map(PricePoint::getPricePerKWh)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCost = totalPriceSum.multiply(energyNeeded)
                .divide(BigDecimal.valueOf(requiredHours), 2, RoundingMode.HALF_UP);
        BigDecimal avgPrice = totalPriceSum.divide(BigDecimal.valueOf(requiredHours), 6, RoundingMode.HALF_UP);
        
        return new ChargingPlan(
                ChargingPlan.ChargingMode.DISCRETE,
                requiredHours,
                startTime,
                selectedHours,
                totalCost,
                avgPrice,
                deadline
        );
    }

    private ZonedDateTime calculateEarliestFeasibleDeadline(List<PricePoint> pricePoints, int requiredHours) {
        if (pricePoints.size() >= requiredHours) {
            return pricePoints.get(requiredHours - 1).getHour().plusHours(1);
        }
        return pricePoints.isEmpty() ? 
            ZonedDateTime.now(timezone).plusHours(requiredHours) :
            pricePoints.get(pricePoints.size() - 1).getHour().plusHours(requiredHours);
    }
}