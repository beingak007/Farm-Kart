package com.farmkart.client.dto.order;

import com.farmkart.client.enums.OrderPaymentStatus;
import com.farmkart.client.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long buyerId,
        Long vendorId,
        BigDecimal totalAmount,
        OrderStatus status,
        OrderPaymentStatus paymentStatus,
        String shippingAddress,
        List<OrderItemResponse> items,
        Instant createdAt
) {
    public record OrderItemResponse(
            Long productId,
            BigDecimal quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {
    }
}
