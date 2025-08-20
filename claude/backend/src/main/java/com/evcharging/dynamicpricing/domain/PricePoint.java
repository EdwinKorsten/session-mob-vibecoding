package com.evcharging.dynamicpricing.domain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PricePoint(
    ZonedDateTime hour,
    BigDecimal pricePerKWh
) {
}