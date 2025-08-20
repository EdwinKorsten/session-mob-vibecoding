package com.evcharging.dynamicpricing.web.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record PricePointDto(
    ZonedDateTime hour,
    BigDecimal pricePerKWh
) {
}