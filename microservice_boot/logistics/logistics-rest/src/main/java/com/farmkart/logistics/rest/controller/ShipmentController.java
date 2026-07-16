package com.farmkart.logistics.rest.controller;

import com.farmkart.logistics.client.dto.CreateShipmentRequest;
import com.farmkart.logistics.client.dto.ShipmentResponse;
import com.farmkart.logistics.service.ShipmentService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@Tag(name = "Logistics", description = "Shipment creation and delivery tracking")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    public ApiResponse<ShipmentResponse> create(@RequestBody @Valid CreateShipmentRequest req) {
        return ApiResponse.ok(shipmentService.createShipment(req));
    }

    @GetMapping("/track/{trackingNumber}")
    public ApiResponse<ShipmentResponse> track(@PathVariable String trackingNumber) {
        return ApiResponse.ok(shipmentService.track(trackingNumber));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<List<ShipmentResponse>> getByOrder(@PathVariable Long orderId) {
        return ApiResponse.ok(shipmentService.getByOrderId(orderId));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<Page<ShipmentResponse>> listByStatus(@PathVariable String status, Pageable pageable) {
        return ApiResponse.ok(shipmentService.listByStatus(status, pageable));
    }

    @PatchMapping("/{shipmentId}/status")
    public ApiResponse<ShipmentResponse> updateStatus(@PathVariable Long shipmentId,
                                                       @RequestParam String status) {
        return ApiResponse.ok(shipmentService.updateStatus(shipmentId, status));
    }
}
