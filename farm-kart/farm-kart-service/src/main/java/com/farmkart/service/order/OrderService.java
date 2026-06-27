package com.farmkart.service.order;

import com.farmkart.starter.common.exception.BusinessException;
import com.farmkart.client.dto.order.CreateOrderRequest;
import com.farmkart.client.dto.order.OrderResponse;
import com.farmkart.repository.entity.Order;
import com.farmkart.repository.entity.OrderItem;
import com.farmkart.client.enums.OrderStatus;
import com.farmkart.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setBuyerId(request.buyerId());
        order.setVendorId(request.vendorId());
        order.setShippingAddress(request.shippingAddress());

        BigDecimal total = BigDecimal.ZERO;
        for (CreateOrderRequest.CartItemRequest item : request.cartItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(item.productId());
            orderItem.setQuantity(item.quantity());
            orderItem.setUnitPrice(item.unitPrice());
            BigDecimal subtotal = item.unitPrice().multiply(item.quantity());
            orderItem.setSubtotal(subtotal);
            order.getItems().add(orderItem);
            total = total.add(subtotal);
        }
        order.setTotalAmount(total);
        orderRepository.save(order);
        return toResponse(order);
    }

    public OrderResponse getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Order not found"));
        return toResponse(order);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "Order not found"));

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Order cannot be cancelled");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        var items = order.getItems().stream()
                .map(i -> new OrderResponse.OrderItemResponse(
                        i.getProductId(), i.getQuantity(), i.getUnitPrice(), i.getSubtotal()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getBuyerId(),
                order.getVendorId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getShippingAddress(),
                items,
                order.getCreatedAt());
    }
}
