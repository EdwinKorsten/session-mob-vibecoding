package com.evcharging.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

public class PricePoint {
    private final ZonedDateTime hour;
    private final BigDecimal pricePerKWh;

    public PricePoint(ZonedDateTime hour, BigDecimal pricePerKWh) {
        this.hour = Objects.requireNonNull(hour, "Hour cannot be null");
        this.pricePerKWh = Objects.requireNonNull(pricePerKWh, "Price cannot be null");
    }

    public ZonedDateTime getHour() {
        return hour;
    }

    public BigDecimal getPricePerKWh() {
        return pricePerKWh;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PricePoint that = (PricePoint) o;
        return Objects.equals(hour, that.hour) && Objects.equals(pricePerKWh, that.pricePerKWh);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, pricePerKWh);
    }

    @Override
    public String toString() {
        return "PricePoint{" +
                "hour=" + hour +
                ", pricePerKWh=" + pricePerKWh +
                '}';
    }
}