package com.farmkart.rest.controller;

import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.client.dto.order.CreateOrderRequest;
import com.farmkart.client.dto.order.OrderResponse;
import com.farmkart.service.order.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok("Order placed", orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Long id) {
        return ApiResponse.ok(orderService.getById(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ApiResponse.ok("Order cancelled", orderService.cancel(id));
    }
}
