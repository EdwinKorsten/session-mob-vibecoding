package com.evcharging.web.controller;

import com.evcharging.domain.ChargingPlan;
import com.evcharging.domain.ChargingPlanRequest;
import com.evcharging.service.ChargingPlanService;
import com.evcharging.web.dto.ChargingPlanDto;
import com.evcharging.web.dto.ChargingPlanRequestDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plan")
@CrossOrigin(origins = "http://localhost:3000")
public class ChargingPlanController {
    private final ChargingPlanService chargingPlanService;

    public ChargingPlanController(ChargingPlanService chargingPlanService) {
        this.chargingPlanService = chargingPlanService;
    }

    @PostMapping
    public ResponseEntity<ChargingPlanDto> calculateChargingPlan(
            @Valid @RequestBody ChargingPlanRequestDto requestDto) {
        
        try {
            ChargingPlanRequest request = new ChargingPlanRequest(
                    requestDto.getDate(),
                    requestDto.getDeadline(),
                    requestDto.getTimezone(),
                    requestDto.getChargeRateKwhPerHour(),
                    requestDto.getEnergyNeededKwh(),
                    requestDto.getContinuous()
            );
            
            ChargingPlan plan = chargingPlanService.calculateOptimalChargingPlan(request);
            
            ChargingPlanDto dto = new ChargingPlanDto(
                    plan.getMode(),
                    plan.getRequiredHours(),
                    plan.getStartTime(),
                    plan.getSelectedHours(),
                    plan.getTotalCostEur(),
                    plan.getAvgPriceEurPerKwh(),
                    plan.getDeadline()
            );
            
            return ResponseEntity.ok(dto);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}