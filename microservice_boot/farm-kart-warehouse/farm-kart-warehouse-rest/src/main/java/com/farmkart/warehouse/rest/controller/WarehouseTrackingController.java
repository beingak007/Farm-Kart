package com.farmkart.warehouse.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.warehouse.client.dto.BookingLiveLocationResponse;
import com.farmkart.warehouse.client.dto.PostBookingLocationRequest;
import com.farmkart.warehouse.service.WarehouseLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouses/bookings")
@Tag(name = "Warehouse Tracking", description = "Live location after booking")
public class WarehouseTrackingController {

    private final WarehouseLocationService locationService;

    public WarehouseTrackingController(WarehouseLocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/{bookingId}/live-location")
    @Operation(summary = "Get latest warehouse live location for a booking")
    public ApiResponse<BookingLiveLocationResponse> getLive(@PathVariable Long bookingId) {
        return ApiResponse.ok(locationService.getLiveLocation(bookingId));
    }

    @GetMapping("/farmer/{farmerId}/live-location")
    public ApiResponse<BookingLiveLocationResponse> getLiveForFarmer(@PathVariable Long farmerId) {
        return ApiResponse.ok(locationService.getLiveLocationForFarmer(farmerId));
    }

    @PostMapping("/{bookingId}/live-location")
    @Operation(summary = "Update warehouse live location (owner/transporter) — notifies farmer")
    public ApiResponse<BookingLiveLocationResponse> updateLive(@PathVariable Long bookingId,
                                                                @RequestBody @Valid PostBookingLocationRequest req) {
        return ApiResponse.ok(locationService.updateLocation(bookingId, req));
    }
}
