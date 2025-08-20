package com.evcharging.dynamicpricing.web;

import com.evcharging.dynamicpricing.service.ChargingPlanService;
import com.evcharging.dynamicpricing.web.dto.ChargingPlanRequest;
import com.evcharging.dynamicpricing.web.dto.ChargingPlanResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ChargingPlanController {

    private final ChargingPlanService chargingPlanService;

    public ChargingPlanController(ChargingPlanService chargingPlanService) {
        this.chargingPlanService = chargingPlanService;
    }

    @PostMapping("/plan")
    public ResponseEntity<ChargingPlanResponse> calculatePlan(@Valid @RequestBody ChargingPlanRequest request) {
        try {
            ChargingPlanResponse response = chargingPlanService.calculatePlan(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}