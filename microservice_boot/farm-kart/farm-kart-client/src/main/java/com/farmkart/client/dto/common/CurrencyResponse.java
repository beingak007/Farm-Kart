package com.farmkart.client.dto.common;

public record CurrencyResponse(
        String code,
        String displayName,
        String symbol
) {}
