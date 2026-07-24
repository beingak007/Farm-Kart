package com.farmkart.market_price.rest.controller;

import com.farmkart.market_price.client.dto.MandiPriceResponse;
import com.farmkart.market_price.service.MandiPriceService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/market-prices")
@Tag(name = "Market Price", description = "Mandi rates and historical price analytics")
public class MandiPriceController {

    private final MandiPriceService priceService;

    public MandiPriceController(MandiPriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/latest")
    public ApiResponse<List<MandiPriceResponse>> getLatest(
            @RequestParam String cropName,
            @RequestParam String state) {
        return ApiResponse.ok(priceService.getLatestByCropAndState(cropName, state));
    }

    @GetMapping("/history")
    public ApiResponse<List<MandiPriceResponse>> getHistory(
            @RequestParam String cropName,
            @RequestParam String state,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.ok(priceService.getPriceHistory(cropName, state, from, to));
    }
}
