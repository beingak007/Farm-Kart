package com.farmkart.product_catalog.rest.controller;

import com.farmkart.product_catalog.client.dto.CreateCategoryRequest;
import com.farmkart.product_catalog.client.dto.CreateCropRequest;
import com.farmkart.product_catalog.client.dto.CropResponse;
import com.farmkart.product_catalog.repository.entity.CropCategory;
import com.farmkart.product_catalog.service.CropCatalogService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
@Tag(name = "Product Catalog", description = "Crop catalog and category management")
public class CropController {

    private final CropCatalogService catalogService;

    public CropController(CropCatalogService catalogService) {
        this.catalogService = catalogService;
    }

    // ── Categories ────────────────────────────────────────────────────────

    @PostMapping("/categories")
    @Operation(summary = "Create crop category")
    public ApiResponse<Long> createCategory(@RequestBody @Valid CreateCategoryRequest req) {
        return ApiResponse.ok(catalogService.createCategory(req));
    }

    @GetMapping("/categories")
    @Operation(summary = "List all active categories")
    public ApiResponse<List<CropCategory>> listCategories() {
        return ApiResponse.ok(catalogService.listActiveCategories());
    }

    // ── Crops ─────────────────────────────────────────────────────────────

    @PostMapping("/crops")
    @Operation(summary = "Add a crop to the catalog")
    public ApiResponse<CropResponse> createCrop(@RequestBody @Valid CreateCropRequest req) {
        return ApiResponse.ok(catalogService.createCrop(req));
    }

    @GetMapping("/crops/{cropId}")
    @Operation(summary = "Get crop by ID")
    public ApiResponse<CropResponse> getById(@PathVariable Long cropId) {
        return ApiResponse.ok(catalogService.getById(cropId));
    }

    @GetMapping("/crops/slug/{slug}")
    @Operation(summary = "Get crop by URL slug")
    public ApiResponse<CropResponse> getBySlug(@PathVariable String slug) {
        return ApiResponse.ok(catalogService.getBySlug(slug));
    }

    @GetMapping("/crops/search")
    @Operation(summary = "Full-text search across crops")
    public ApiResponse<Page<CropResponse>> search(@RequestParam String q, Pageable pageable) {
        return ApiResponse.ok(catalogService.search(q, pageable));
    }

    @GetMapping("/crops/category/{categoryId}")
    @Operation(summary = "List crops by category")
    public ApiResponse<Page<CropResponse>> listByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return ApiResponse.ok(catalogService.listByCategory(categoryId, pageable));
    }

    @GetMapping("/crops/organic")
    @Operation(summary = "List all organic crops")
    public ApiResponse<Page<CropResponse>> listOrganic(Pageable pageable) {
        return ApiResponse.ok(catalogService.listOrganic(pageable));
    }
}
