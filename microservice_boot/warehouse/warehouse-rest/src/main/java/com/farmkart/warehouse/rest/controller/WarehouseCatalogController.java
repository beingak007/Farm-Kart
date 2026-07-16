package com.farmkart.warehouse.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.warehouse.client.dto.*;
import com.farmkart.warehouse.service.WarehouseCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses/{warehouseId}")
@Tag(name = "Warehouse Catalog", description = "Photos, ratings, comments (PostgreSQL app data)")
public class WarehouseCatalogController {

    private final WarehouseCatalogService catalogService;

    public WarehouseCatalogController(WarehouseCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/detail")
    @Operation(summary = "Pre-booking profile: photos, ratings, comments")
    public ApiResponse<WarehouseDetailResponse> detail(@PathVariable Long warehouseId) {
        return ApiResponse.ok(catalogService.getDetail(warehouseId));
    }

    @PostMapping("/photos")
    public ApiResponse<WarehousePhotoResponse> addPhoto(@PathVariable Long warehouseId,
                                                         @RequestBody @Valid AddWarehousePhotoRequest req) {
        return ApiResponse.ok(catalogService.addPhoto(warehouseId, req));
    }

    @PostMapping("/ratings")
    public ApiResponse<WarehouseRatingResponse> addRating(@PathVariable Long warehouseId,
                                                           @RequestBody @Valid WarehouseRatingRequest req) {
        return ApiResponse.ok(catalogService.addRating(warehouseId, req));
    }

    @PostMapping("/comments")
    public ApiResponse<WarehouseCommentResponse> addComment(@PathVariable Long warehouseId,
                                                             @RequestBody @Valid WarehouseCommentRequest req) {
        return ApiResponse.ok(catalogService.addComment(warehouseId, req));
    }
}
