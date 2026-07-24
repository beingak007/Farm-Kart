package com.farmkart.warehouse.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.warehouse.client.dto.*;
import com.farmkart.warehouse.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/warehouses")
@Tag(name = "Warehouse", description = "Storage and cold-storage booking")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/state/{state}")
    public ApiResponse<Page<WarehouseResponse>> listByState(@PathVariable String state, Pageable pageable) {
        return ApiResponse.ok(warehouseService.listByState(state, pageable));
    }

    @GetMapping("/available")
    public ApiResponse<List<WarehouseResponse>> findAvailable(@RequestParam String state,
                                                               @RequestParam double tons) {
        return ApiResponse.ok(warehouseService.findAvailable(state, tons));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find available warehouses within radius (default 100 km), nearest first")
    public ApiResponse<List<NearbyWarehouseResponse>> findNearby(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam double tons,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "100") double radiusKm) {
        return ApiResponse.ok(warehouseService.findNearby(lat, lng, tons, startDate, endDate, radiusKm));
    }

    @PostMapping("/smart-book")
    @Operation(summary = "Uber-style book: match nearest warehouse within 100 km and send notification")
    public ApiResponse<SmartBookWarehouseResponse> smartBook(@RequestBody @Valid SmartBookWarehouseRequest req) {
        return ApiResponse.ok(warehouseService.smartBook(req));
    }

    @PostMapping("/book")
    public ApiResponse<Long> book(@RequestBody @Valid BookWarehouseRequest req) {
        return ApiResponse.ok(warehouseService.book(req));
    }
}
