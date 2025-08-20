package com.evcharging.web.dto;

import com.evcharging.domain.ChargingPlan;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class ChargingPlanDto {
    private ChargingPlan.ChargingMode mode;
    private int requiredHours;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime startTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private List<ZonedDateTime> selectedHours;
    
    private BigDecimal totalCostEur;
    private BigDecimal avgPriceEurPerKwh;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime deadline;

    public ChargingPlanDto() {}

    public ChargingPlanDto(ChargingPlan.ChargingMode mode, int requiredHours, ZonedDateTime startTime,
                          List<ZonedDateTime> selectedHours, BigDecimal totalCostEur, 
                          BigDecimal avgPriceEurPerKwh, ZonedDateTime deadline) {
        this.mode = mode;
        this.requiredHours = requiredHours;
        this.startTime = startTime;
        this.selectedHours = selectedHours;
        this.totalCostEur = totalCostEur;
        this.avgPriceEurPerKwh = avgPriceEurPerKwh;
        this.deadline = deadline;
    }

    public ChargingPlan.ChargingMode getMode() {
        return mode;
    }

    public void setMode(ChargingPlan.ChargingMode mode) {
        this.mode = mode;
    }

    public int getRequiredHours() {
        return requiredHours;
    }

    public void setRequiredHours(int requiredHours) {
        this.requiredHours = requiredHours;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    public List<ZonedDateTime> getSelectedHours() {
        return selectedHours;
    }

    public void setSelectedHours(List<ZonedDateTime> selectedHours) {
        this.selectedHours = selectedHours;
    }

    public BigDecimal getTotalCostEur() {
        return totalCostEur;
    }

    public void setTotalCostEur(BigDecimal totalCostEur) {
        this.totalCostEur = totalCostEur;
    }

    public BigDecimal getAvgPriceEurPerKwh() {
        return avgPriceEurPerKwh;
    }

    public void setAvgPriceEurPerKwh(BigDecimal avgPriceEurPerKwh) {
        this.avgPriceEurPerKwh = avgPriceEurPerKwh;
    }

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(ZonedDateTime deadline) {
        this.deadline = deadline;
    }
}