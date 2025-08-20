package com.evcharging.web.controller;

import com.evcharging.domain.PricePoint;
import com.evcharging.service.EnergyChartsClient;
import com.evcharging.web.dto.PricePointDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/prices")
@CrossOrigin(origins = "http://localhost:3000")
public class PricesController {
    private final EnergyChartsClient energyChartsClient;

    public PricesController(EnergyChartsClient energyChartsClient) {
        this.energyChartsClient = energyChartsClient;
    }

    @GetMapping
    public ResponseEntity<List<PricePointDto>> getPrices(
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
            LocalDate date) {
        
        if (date == null) {
            date = LocalDate.now();
        }
        
        try {
            List<PricePoint> pricePoints = energyChartsClient.fetchPrices(date);
            List<PricePointDto> dtos = pricePoints.stream()
                    .map(point -> new PricePointDto(point.getHour(), point.getPricePerKWh()))
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}