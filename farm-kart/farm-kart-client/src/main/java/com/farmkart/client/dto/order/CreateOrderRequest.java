package com.farmkart.client.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotNull Long buyerId,
        @NotNull Long vendorId,
        @NotBlank String shippingAddress,
        @NotEmpty @Valid List<CartItemRequest> cartItems
) {
    public record CartItemRequest(
            @NotNull Long productId,
            @NotNull @DecimalMin("0.001") BigDecimal quantity,
            @NotNull @DecimalMin("0.01") BigDecimal unitPrice
    ) {
    }
}
