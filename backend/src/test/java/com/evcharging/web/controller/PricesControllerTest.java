package com.evcharging.web.controller;

import com.evcharging.domain.PricePoint;
import com.evcharging.service.EnergyChartsClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PricesController.class)
class PricesControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private EnergyChartsClient energyChartsClient;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void shouldReturnPricesForGivenDate() throws Exception {
        LocalDate testDate = LocalDate.of(2025, 8, 20);
        ZoneId zone = ZoneId.of("Europe/Amsterdam");
        
        List<PricePoint> pricePoints = Arrays.asList(
            new PricePoint(testDate.atTime(6, 0).atZone(zone), BigDecimal.valueOf(0.154)),
            new PricePoint(testDate.atTime(7, 0).atZone(zone), BigDecimal.valueOf(0.162))
        );
        
        when(energyChartsClient.fetchPrices(any(LocalDate.class))).thenReturn(pricePoints);
        
        mockMvc.perform(get("/api/prices")
                .param("date", "2025-08-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].hour").value("2025-08-20T06:00:00+02:00"))
                .andExpect(jsonPath("$[0].pricePerKWh").value(0.154))
                .andExpect(jsonPath("$[1].hour").value("2025-08-20T07:00:00+02:00"))
                .andExpect(jsonPath("$[1].pricePerKWh").value(0.162));
    }
    
    @Test
    void shouldReturnPricesForTodayWhenNoDateProvided() throws Exception {
        List<PricePoint> pricePoints = Arrays.asList(
            new PricePoint(LocalDate.now().atTime(6, 0).atZone(ZoneId.of("Europe/Amsterdam")), 
                         BigDecimal.valueOf(0.154))
        );
        
        when(energyChartsClient.fetchPrices(any(LocalDate.class))).thenReturn(pricePoints);
        
        mockMvc.perform(get("/api/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}