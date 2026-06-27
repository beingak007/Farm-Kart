package com.farmkart.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.client.dto.vendor.VendorOnboardRequest;
import com.farmkart.client.dto.vendor.VendorResponse;
import com.farmkart.service.vendor.VendorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vendors")
@Tag(name = "Vendor")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping
    public ApiResponse<VendorResponse> onboard(@Valid @RequestBody VendorOnboardRequest request) {
        return ApiResponse.ok("Vendor onboarding submitted for approval", vendorService.onboard(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<VendorResponse> getById(@PathVariable Long id) {
        return ApiResponse.ok(vendorService.getById(id));
    }
}
