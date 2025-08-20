package com.evcharging.dynamicpricing.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ChargingPlanRequest(
    @NotNull LocalDate date,
    @NotNull LocalTime deadline,
    @NotNull String timezone,
    @DecimalMin(value = "0.1") double chargeRateKwhPerHour,
    @DecimalMin(value = "0.1") double energyNeededKwh,
    boolean continuous
) {
}