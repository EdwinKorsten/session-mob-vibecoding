package com.evcharging.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PricePointDto {
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime hour;
    
    @NotNull
    private BigDecimal pricePerKWh;

    public PricePointDto() {}

    public PricePointDto(ZonedDateTime hour, BigDecimal pricePerKWh) {
        this.hour = hour;
        this.pricePerKWh = pricePerKWh;
    }

    public ZonedDateTime getHour() {
        return hour;
    }

    public void setHour(ZonedDateTime hour) {
        this.hour = hour;
    }

    public BigDecimal getPricePerKWh() {
        return pricePerKWh;
    }

    public void setPricePerKWh(BigDecimal pricePerKWh) {
        this.pricePerKWh = pricePerKWh;
    }
}