package com.evcharging.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ChargingPlanRequest {
    private final LocalDate date;
    private final LocalTime deadline;
    private final String timezone;
    private final BigDecimal chargeRateKwhPerHour;
    private final BigDecimal energyNeededKwh;
    private final boolean continuous;

    public ChargingPlanRequest(LocalDate date, LocalTime deadline, String timezone,
                              BigDecimal chargeRateKwhPerHour, BigDecimal energyNeededKwh,
                              boolean continuous) {
        this.date = date;
        this.deadline = deadline;
        this.timezone = timezone;
        this.chargeRateKwhPerHour = chargeRateKwhPerHour;
        this.energyNeededKwh = energyNeededKwh;
        this.continuous = continuous;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    public String getTimezone() {
        return timezone;
    }

    public BigDecimal getChargeRateKwhPerHour() {
        return chargeRateKwhPerHour;
    }

    public BigDecimal getEnergyNeededKwh() {
        return energyNeededKwh;
    }

    public boolean isContinuous() {
        return continuous;
    }
}