package com.farmkart.farmer.rest.controller;

import com.farmkart.farmer.client.dto.FarmerOnboardRequest;
import com.farmkart.farmer.client.dto.FarmerResponse;
import com.farmkart.farmer.client.dto.FarmUpdateRequest;
import com.farmkart.farmer.client.enums.FarmerStatus;
import com.farmkart.farmer.service.FarmerService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/farmers")
@Tag(name = "Farmer", description = "Farmer onboarding and profile management")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @PostMapping
    @Operation(summary = "Onboard a new farmer")
    public ApiResponse<FarmerResponse> onboard(@RequestBody @Valid FarmerOnboardRequest req) {
        return ApiResponse.ok(farmerService.onboard(req));
    }

    @GetMapping("/{farmerId}")
    @Operation(summary = "Get farmer by ID")
    public ApiResponse<FarmerResponse> getById(@PathVariable Long farmerId) {
        return ApiResponse.ok(farmerService.getById(farmerId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get farmer profile by user ID")
    public ApiResponse<FarmerResponse> getByUserId(@PathVariable Long userId) {
        return ApiResponse.ok(farmerService.getByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "List all farmers (paginated)")
    public ApiResponse<Page<FarmerResponse>> listAll(Pageable pageable) {
        return ApiResponse.ok(farmerService.listAll(pageable));
    }

    @GetMapping("/state/{state}")
    @Operation(summary = "List farmers by state")
    public ApiResponse<Page<FarmerResponse>> listByState(@PathVariable String state, Pageable pageable) {
        return ApiResponse.ok(farmerService.listByState(state, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List farmers by verification status")
    public ApiResponse<Page<FarmerResponse>> listByStatus(@PathVariable FarmerStatus status, Pageable pageable) {
        return ApiResponse.ok(farmerService.listByStatus(status, pageable));
    }

    @PutMapping("/{farmerId}")
    @Operation(summary = "Update farm details")
    public ApiResponse<FarmerResponse> update(@PathVariable Long farmerId,
                                               @RequestBody FarmUpdateRequest req) {
        return ApiResponse.ok(farmerService.updateFarm(farmerId, req));
    }

    @PatchMapping("/{farmerId}/verify")
    @Operation(summary = "Mark farmer as verified (admin)")
    public ApiResponse<FarmerResponse> verify(@PathVariable Long farmerId) {
        return ApiResponse.ok(farmerService.verify(farmerId));
    }

    @PatchMapping("/{farmerId}/suspend")
    @Operation(summary = "Suspend a farmer account")
    public ApiResponse<FarmerResponse> suspend(@PathVariable Long farmerId) {
        return ApiResponse.ok(farmerService.suspend(farmerId));
    }
}
