package com.farmkart.buyer.rest.controller;

import com.farmkart.buyer.client.dto.BuyerOnboardRequest;
import com.farmkart.buyer.client.dto.BuyerResponse;
import com.farmkart.buyer.service.BuyerService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/buyers")
@Tag(name = "Buyer", description = "Buyer onboarding and profile management")
public class BuyerController {

    private final BuyerService buyerService;

    public BuyerController(BuyerService buyerService) {
        this.buyerService = buyerService;
    }

    @PostMapping
    public ApiResponse<BuyerResponse> onboard(@RequestBody @Valid BuyerOnboardRequest req) {
        return ApiResponse.ok(buyerService.onboard(req));
    }

    @GetMapping("/{buyerId}")
    public ApiResponse<BuyerResponse> getById(@PathVariable Long buyerId) {
        return ApiResponse.ok(buyerService.getById(buyerId));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<BuyerResponse> getByUserId(@PathVariable Long userId) {
        return ApiResponse.ok(buyerService.getByUserId(userId));
    }
}
