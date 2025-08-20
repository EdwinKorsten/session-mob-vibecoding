package com.evcharging.service;

import com.evcharging.domain.ChargingPlan;
import com.evcharging.domain.ChargingPlanRequest;
import com.evcharging.domain.PricePoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChargingPlanServiceTest {
    
    @Mock
    private EnergyChartsClient energyChartsClient;
    
    private ChargingPlanService chargingPlanService;
    
    @BeforeEach
    void setUp() {
        chargingPlanService = new ChargingPlanService(energyChartsClient, "Europe/Amsterdam");
    }
    
    @Test
    void shouldCalculateContinuousChargingPlan() {
        LocalDate testDate = LocalDate.of(2025, 8, 20);
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        
        List<PricePoint> pricePoints = Arrays.asList(
            new PricePoint(testDate.atTime(6, 0).atZone(zone), BigDecimal.valueOf(0.10)),
            new PricePoint(testDate.atTime(7, 0).atZone(zone), BigDecimal.valueOf(0.12)),
            new PricePoint(testDate.atTime(8, 0).atZone(zone), BigDecimal.valueOf(0.15)),
            new PricePoint(testDate.atTime(9, 0).atZone(zone), BigDecimal.valueOf(0.20)),
            new PricePoint(testDate.atTime(10, 0).atZone(zone), BigDecimal.valueOf(0.25)),
            new PricePoint(testDate.atTime(11, 0).atZone(zone), BigDecimal.valueOf(0.30))
        );
        
        when(energyChartsClient.fetchPrices(any(LocalDate.class))).thenReturn(pricePoints);
        
        ChargingPlanRequest request = new ChargingPlanRequest(
            testDate,
            LocalTime.of(14, 0),
            "Europe/Amsterdam",
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(40),
            true
        );
        
        ChargingPlan result = chargingPlanService.calculateOptimalChargingPlan(request);
        
        assertThat(result.getMode()).isEqualTo(ChargingPlan.ChargingMode.CONTINUOUS);
        assertThat(result.getRequiredHours()).isEqualTo(2);
        assertThat(result.getStartTime()).isEqualTo(testDate.atTime(6, 0).atZone(zone));
        assertThat(result.getSelectedHours()).hasSize(2);
        assertThat(result.getTotalCostEur()).isGreaterThan(BigDecimal.ZERO);
    }
    
    @Test
    void shouldCalculateDiscreteChargingPlan() {
        LocalDate testDate = LocalDate.of(2025, 8, 20);
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        
        List<PricePoint> pricePoints = Arrays.asList(
            new PricePoint(testDate.atTime(6, 0).atZone(zone), BigDecimal.valueOf(0.10)),
            new PricePoint(testDate.atTime(7, 0).atZone(zone), BigDecimal.valueOf(0.30)),
            new PricePoint(testDate.atTime(8, 0).atZone(zone), BigDecimal.valueOf(0.12)),
            new PricePoint(testDate.atTime(9, 0).atZone(zone), BigDecimal.valueOf(0.25)),
            new PricePoint(testDate.atTime(10, 0).atZone(zone), BigDecimal.valueOf(0.15))
        );
        
        when(energyChartsClient.fetchPrices(any(LocalDate.class))).thenReturn(pricePoints);
        
        ChargingPlanRequest request = new ChargingPlanRequest(
            testDate,
            LocalTime.of(14, 0),
            "Europe/Amsterdam",
            BigDecimal.valueOf(20),
            BigDecimal.valueOf(40),
            false
        );
        
        ChargingPlan result = chargingPlanService.calculateOptimalChargingPlan(request);
        
        assertThat(result.getMode()).isEqualTo(ChargingPlan.ChargingMode.DISCRETE);
        assertThat(result.getRequiredHours()).isEqualTo(2);
        assertThat(result.getSelectedHours()).containsExactly(
            testDate.atTime(6, 0).atZone(zone),
            testDate.atTime(8, 0).atZone(zone)
        );
    }
    
    @Test
    void shouldThrowExceptionWhenNotEnoughHoursAvailable() {
        LocalDate testDate = LocalDate.of(2025, 8, 20);
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        
        List<PricePoint> pricePoints = Arrays.asList(
            new PricePoint(testDate.atTime(6, 0).atZone(zone), BigDecimal.valueOf(0.10))
        );
        
        when(energyChartsClient.fetchPrices(any(LocalDate.class))).thenReturn(pricePoints);
        
        ChargingPlanRequest request = new ChargingPlanRequest(
            testDate,
            LocalTime.of(8, 0),
            "Europe/Amsterdam",
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(40),
            true
        );
        
        assertThatThrownBy(() -> chargingPlanService.calculateOptimalChargingPlan(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Not enough hours available before deadline");
    }
}