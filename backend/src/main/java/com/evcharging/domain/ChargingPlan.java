package com.evcharging.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class ChargingPlan {
    private final ChargingMode mode;
    private final int requiredHours;
    private final ZonedDateTime startTime;
    private final List<ZonedDateTime> selectedHours;
    private final BigDecimal totalCostEur;
    private final BigDecimal avgPriceEurPerKwh;
    private final ZonedDateTime deadline;

    public ChargingPlan(ChargingMode mode, int requiredHours, ZonedDateTime startTime, 
                       List<ZonedDateTime> selectedHours, BigDecimal totalCostEur, 
                       BigDecimal avgPriceEurPerKwh, ZonedDateTime deadline) {
        this.mode = Objects.requireNonNull(mode);
        this.requiredHours = requiredHours;
        this.startTime = startTime;
        this.selectedHours = Objects.requireNonNull(selectedHours);
        this.totalCostEur = Objects.requireNonNull(totalCostEur);
        this.avgPriceEurPerKwh = Objects.requireNonNull(avgPriceEurPerKwh);
        this.deadline = Objects.requireNonNull(deadline);
    }

    public ChargingMode getMode() {
        return mode;
    }

    public int getRequiredHours() {
        return requiredHours;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public List<ZonedDateTime> getSelectedHours() {
        return selectedHours;
    }

    public BigDecimal getTotalCostEur() {
        return totalCostEur;
    }

    public BigDecimal getAvgPriceEurPerKwh() {
        return avgPriceEurPerKwh;
    }

    public ZonedDateTime getDeadline() {
        return deadline;
    }

    public enum ChargingMode {
        CONTINUOUS, DISCRETE
    }
}