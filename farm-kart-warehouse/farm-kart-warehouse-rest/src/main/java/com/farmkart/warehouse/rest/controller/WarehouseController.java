package com.farmkart.warehouse.rest.controller;

import com.farmkart.warehouse.client.dto.BookWarehouseRequest;
import com.farmkart.warehouse.client.dto.WarehouseResponse;
import com.farmkart.warehouse.service.WarehouseService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/book")
    public ApiResponse<Long> book(@RequestBody @Valid BookWarehouseRequest req) {
        return ApiResponse.ok(warehouseService.book(req));
    }
}
