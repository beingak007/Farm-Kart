package com.farmkart.admin.service;

import com.farmkart.admin.client.dto.CreateAuditLogRequest;
import com.farmkart.admin.repository.AuditLogRepository;
import com.farmkart.admin.repository.entity.AuditLog;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes platform domain events and persists immutable audit records.
 */
@Component
public class PlatformAuditConsumer {

    private static final Logger log = LoggerFactory.getLogger(PlatformAuditConsumer.class);

    private final AuditLogService auditLogService;
    private final DurableEventGuard eventGuard;

    public PlatformAuditConsumer(AuditLogService auditLogService, DurableEventGuard eventGuard) {
        this.auditLogService = auditLogService;
        this.eventGuard = eventGuard;
    }

    @KafkaListener(topics = FkTopics.USER_REGISTERED, groupId = "admin-audit")
    public void onUserRegistered(UserRegisteredEvent event) {
        handle(event.base(), FkTopics.USER_REGISTERED, event.userId(), "User", String.valueOf(event.userId()),
                "User registered: " + event.email());
    }

    @KafkaListener(topics = FkTopics.FARMER_CREATED, groupId = "admin-audit")
    public void onFarmerCreated(FarmerCreatedEvent event) {
        handle(event.base(), FkTopics.FARMER_CREATED, event.userId(), "Farmer", String.valueOf(event.farmerId()),
                "Farmer onboarded: " + event.farmName() + " (" + event.state() + ")");
    }

    @KafkaListener(topics = FkTopics.FARMER_VERIFIED, groupId = "admin-audit")
    public void onFarmerVerified(FkBaseEvent event) {
        handle(event, FkTopics.FARMER_VERIFIED, null, "Farmer", null, "Farmer profile verified");
    }

    @KafkaListener(topics = FkTopics.BUYER_CREATED, groupId = "admin-audit")
    public void onBuyerCreated(BuyerCreatedEvent event) {
        handle(event.base(), FkTopics.BUYER_CREATED, event.userId(), "Buyer", String.valueOf(event.buyerId()),
                "Buyer onboarded: " + event.displayName());
    }

    @KafkaListener(topics = FkTopics.ORDER_CREATED, groupId = "admin-audit")
    public void onOrderCreated(OrderCreatedEvent event) {
        handle(event.base(), FkTopics.ORDER_CREATED, event.buyerId(), "Order", String.valueOf(event.orderId()),
                "Order placed — total " + event.totalAmount() + " " + event.currency());
    }

    @KafkaListener(topics = FkTopics.ORDER_CANCELLED, groupId = "admin-audit")
    public void onOrderCancelled(OrderCancelledEvent event) {
        handle(event.base(), FkTopics.ORDER_CANCELLED, event.buyerId(), "Order", String.valueOf(event.orderId()),
                "Order cancelled" + (event.reason() != null ? ": " + event.reason() : ""));
    }

    @KafkaListener(topics = FkTopics.PAYMENT_SUCCESS, groupId = "admin-audit")
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        handle(event.base(), FkTopics.PAYMENT_SUCCESS, event.userId(), "Payment", String.valueOf(event.paymentId()),
                "Payment captured for order " + event.orderId() + " — " + event.amount());
    }

    @KafkaListener(topics = FkTopics.SHIPMENT_CREATED, groupId = "admin-audit")
    public void onShipmentCreated(ShipmentCreatedEvent event) {
        handle(event.base(), FkTopics.SHIPMENT_CREATED, null, "Shipment", String.valueOf(event.shipmentId()),
                "Shipment " + event.trackingNumber() + " created for order " + event.orderId());
    }

    @KafkaListener(topics = FkTopics.SHIPMENT_DELIVERED, groupId = "admin-audit")
    public void onShipmentDelivered(ShipmentDeliveredEvent event) {
        handle(event.base(), FkTopics.SHIPMENT_DELIVERED, null, "Shipment", String.valueOf(event.shipmentId()),
                "Shipment " + event.trackingNumber() + " delivered for order " + event.orderId());
    }

    private void handle(FkBaseEvent base, String topic, Long userId, String resourceType,
                        String resourceId, String description) {
        eventGuard.runOnce(base.eventId(), topic, () -> {
            auditLogService.create(new CreateAuditLogRequest(
                    userId,
                    base.serviceOrigin(),
                    base.eventType(),
                    resourceType,
                    resourceId,
                    null,
                    description,
                    "SUCCESS"));
            log.debug("Audit recorded eventId={} topic={}", base.eventId(), topic);
        });
    }
}
