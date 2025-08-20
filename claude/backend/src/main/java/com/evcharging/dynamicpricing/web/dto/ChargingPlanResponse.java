package com.evcharging.dynamicpricing.web.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record ChargingPlanResponse(
    Mode mode,
    int requiredHours,
    ZonedDateTime startTime,
    List<ZonedDateTime> selectedHours,
    BigDecimal totalCostEur,
    BigDecimal avgPriceEurPerKwh,
    ZonedDateTime deadline
) {
    public enum Mode {
        CONTINUOUS, DISCRETE
    }
}