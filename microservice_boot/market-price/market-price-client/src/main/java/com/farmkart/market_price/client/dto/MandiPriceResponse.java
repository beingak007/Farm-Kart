package com.farmkart.market_price.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MandiPriceResponse(
        Long id,
        String cropName,
        String mandiName,
        String state,
        String district,
        BigDecimal pricePerQuintal,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        LocalDate priceDate,
        String source
) {}
