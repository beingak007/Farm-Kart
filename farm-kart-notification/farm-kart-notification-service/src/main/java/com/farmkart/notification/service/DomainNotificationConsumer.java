package com.farmkart.notification.service;

import com.farmkart.notification.client.enums.NotificationChannel;
import com.farmkart.starter.common.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Reacts to domain events and triggers user notifications asynchronously.
 */
@Component
public class DomainNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(DomainNotificationConsumer.class);

    private final NotificationService notificationService;
    private final DurableEventGuard eventGuard;

    public DomainNotificationConsumer(NotificationService notificationService,
                                      DurableEventGuard eventGuard) {
        this.notificationService = notificationService;
        this.eventGuard = eventGuard;
    }

    @KafkaListener(topics = FkTopics.USER_REGISTERED, groupId = "notification-domain")
    public void onUserRegistered(UserRegisteredEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.USER_REGISTERED, () ->
                notificationService.sendDomainNotification(
                        event.userId(),
                        NotificationChannel.SMS,
                        event.mobile(),
                        "WELCOME",
                        Map.of("name", event.email())));
    }

    @KafkaListener(topics = FkTopics.FARMER_CREATED, groupId = "notification-domain")
    public void onFarmerCreated(FarmerCreatedEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.FARMER_CREATED, () ->
                notificationService.sendDomainNotification(
                        event.userId(),
                        NotificationChannel.PUSH,
                        null,
                        "FARMER_WELCOME",
                        Map.of("farmName", event.farmName(), "state", event.state())));
    }

    @KafkaListener(topics = FkTopics.FARMER_VERIFIED, groupId = "notification-domain")
    public void onFarmerVerified(FkBaseEvent event) {
        eventGuard.runOnce(event.eventId(), FkTopics.FARMER_VERIFIED, () ->
                notificationService.sendDomainNotification(
                        null,
                        NotificationChannel.PUSH,
                        null,
                        "FARMER_VERIFIED",
                        Map.of()));
    }

    @KafkaListener(topics = FkTopics.ORDER_CREATED, groupId = "notification-domain")
    public void onOrderCreated(OrderCreatedEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.ORDER_CREATED, () ->
                notificationService.sendDomainNotification(
                        event.buyerId(),
                        NotificationChannel.PUSH,
                        null,
                        "ORDER_CONFIRMED",
                        Map.of(
                                "orderId", String.valueOf(event.orderId()),
                                "total", event.totalAmount().toPlainString())));
    }

    @KafkaListener(topics = FkTopics.PAYMENT_SUCCESS, groupId = "notification-domain")
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.PAYMENT_SUCCESS, () ->
                notificationService.sendDomainNotification(
                        event.userId(),
                        NotificationChannel.PUSH,
                        null,
                        "PAYMENT_RECEIPT",
                        Map.of(
                                "orderId", String.valueOf(event.orderId()),
                                "amount", event.amount().toPlainString())));
    }

    @KafkaListener(topics = FkTopics.SHIPMENT_DELIVERED, groupId = "notification-domain")
    public void onShipmentDelivered(ShipmentDeliveredEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.SHIPMENT_DELIVERED, () ->
                notificationService.sendDomainNotification(
                        null,
                        NotificationChannel.PUSH,
                        null,
                        "ORDER_DELIVERED",
                        Map.of(
                                "orderId", String.valueOf(event.orderId()),
                                "trackingNumber", event.trackingNumber())));
    }
}
