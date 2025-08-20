package com.evcharging.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ChargingPlanRequestDto {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime deadline;
    
    @NotBlank
    private String timezone = "Europe/Amsterdam";
    
    @NotNull
    @DecimalMin(value = "0.1", message = "Charge rate must be at least 0.1 kWh/h")
    @DecimalMax(value = "350.0", message = "Charge rate cannot exceed 350 kWh/h")
    private BigDecimal chargeRateKwhPerHour = BigDecimal.valueOf(10);
    
    @NotNull
    @DecimalMin(value = "1.0", message = "Energy needed must be at least 1.0 kWh")
    @DecimalMax(value = "1000.0", message = "Energy needed cannot exceed 1000 kWh")
    private BigDecimal energyNeededKwh = BigDecimal.valueOf(80);
    
    @NotNull
    private Boolean continuous = true;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalTime deadline) {
        this.deadline = deadline;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public BigDecimal getChargeRateKwhPerHour() {
        return chargeRateKwhPerHour;
    }

    public void setChargeRateKwhPerHour(BigDecimal chargeRateKwhPerHour) {
        this.chargeRateKwhPerHour = chargeRateKwhPerHour;
    }

    public BigDecimal getEnergyNeededKwh() {
        return energyNeededKwh;
    }

    public void setEnergyNeededKwh(BigDecimal energyNeededKwh) {
        this.energyNeededKwh = energyNeededKwh;
    }

    public Boolean getContinuous() {
        return continuous;
    }

    public void setContinuous(Boolean continuous) {
        this.continuous = continuous;
    }
}