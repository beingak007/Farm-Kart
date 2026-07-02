package com.farmkart.rest.controller;

import com.farmkart.client.dto.common.CurrencyResponse;
import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.starter.common.enums.FkCurrencyEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/currencies")
@Tag(name = "Currency", description = "ISO 4217 currency reference")
public class CurrencyController {

    @GetMapping
    @Operation(summary = "List all supported ISO 4217 currencies")
    public ApiResponse<List<CurrencyResponse>> listAll() {
        List<CurrencyResponse> currencies = Arrays.stream(FkCurrencyEnum.values())
                .map(c -> new CurrencyResponse(c.getValue(), c.getDisplayName(), c.getSymbol()))
                .toList();
        return ApiResponse.ok(currencies);
    }
}
