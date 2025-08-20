package com.evcharging.dynamicpricing.web;

import com.evcharging.dynamicpricing.domain.PricePoint;
import com.evcharging.dynamicpricing.service.EnergyChartsClient;
import com.evcharging.dynamicpricing.web.dto.PricePointDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PricesController {

    private final EnergyChartsClient energyChartsClient;

    public PricesController(EnergyChartsClient energyChartsClient) {
        this.energyChartsClient = energyChartsClient;
    }

    @GetMapping("/prices")
    public ResponseEntity<?> getPrices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            List<PricePoint> prices = energyChartsClient.fetchPrices(date);
            
            List<PricePointDto> priceDto = prices.stream()
                .map(price -> new PricePointDto(price.hour(), price.pricePerKWh()))
                .toList();
                
            return ResponseEntity.ok(priceDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", e.getMessage()));
        }
    }
}