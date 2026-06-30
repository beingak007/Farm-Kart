package com.farmkart.service.order;

import com.farmkart.client.dto.order.CreateOrderRequest;
import com.farmkart.client.dto.order.OrderResponse;
import com.farmkart.client.enums.OrderStatus;
import com.farmkart.repository.OrderRepository;
import com.farmkart.repository.entity.Order;
import com.farmkart.repository.entity.OrderItem;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.OrderCancelledEvent;
import com.farmkart.starter.common.events.OrderCreatedEvent;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final DomainEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, DomainEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
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

        OrderCreatedEvent event = new OrderCreatedEvent(
                new FkBaseEvent(FkTopics.ORDER_CREATED, "marketplace-service"),
                order.getId(), order.getBuyerId(), order.getVendorId(),
                order.getTotalAmount(), "INR", Instant.now());
        eventPublisher.publish(FkTopics.ORDER_CREATED, String.valueOf(order.getId()), event);

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

        OrderCancelledEvent event = new OrderCancelledEvent(
                new FkBaseEvent(FkTopics.ORDER_CANCELLED, "marketplace-service"),
                order.getId(), order.getBuyerId(), "User requested cancellation", Instant.now());
        eventPublisher.publish(FkTopics.ORDER_CANCELLED, String.valueOf(order.getId()), event);

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
