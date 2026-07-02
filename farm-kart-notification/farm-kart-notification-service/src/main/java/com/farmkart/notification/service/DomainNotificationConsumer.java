package com.farmkart.notification.service;

import com.farmkart.notification.client.enums.NotificationChannel;
import com.farmkart.notification.client.enums.NotificationTemplateCodeEnum;
import com.farmkart.notification.client.enums.NotificationTemplateVarEnum;
import com.farmkart.notification.constants.NotificationServiceConstants;
import com.farmkart.starter.common.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

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

    @KafkaListener(topics = FkTopics.USER_REGISTERED, groupId = NotificationServiceConstants.KAFKA_GROUP_DOMAIN)
    public void onUserRegistered(UserRegisteredEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.USER_REGISTERED, () ->
                notificationService.sendDomainNotification(
                        event.userId(),
                        NotificationChannel.SMS,
                        event.mobile(),
                        NotificationTemplateCodeEnum.WELCOME,
                        Map.of(NotificationTemplateVarEnum.NAME.getValue(), event.email())));
    }

    @KafkaListener(topics = FkTopics.FARMER_CREATED, groupId = NotificationServiceConstants.KAFKA_GROUP_DOMAIN)
    public void onFarmerCreated(FarmerCreatedEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.FARMER_CREATED, () ->
                notificationService.sendDomainNotification(
                        event.userId(),
                        NotificationChannel.PUSH,
                        null,
                        NotificationTemplateCodeEnum.FARMER_WELCOME,
                        Map.of(
                                NotificationTemplateVarEnum.FARM_NAME.getValue(), event.farmName(),
                                NotificationTemplateVarEnum.STATE.getValue(), event.state())));
    }

    @KafkaListener(topics = FkTopics.FARMER_VERIFIED, groupId = NotificationServiceConstants.KAFKA_GROUP_DOMAIN)
    public void onFarmerVerified(FkBaseEvent event) {
        eventGuard.runOnce(event.eventId(), FkTopics.FARMER_VERIFIED, () ->
                notificationService.sendDomainNotification(
                        null,
                        NotificationChannel.PUSH,
                        null,
                        NotificationTemplateCodeEnum.FARMER_VERIFIED,
                        Map.of()));
    }

    @KafkaListener(topics = FkTopics.ORDER_CREATED, groupId = NotificationServiceConstants.KAFKA_GROUP_DOMAIN)
    public void onOrderCreated(OrderCreatedEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.ORDER_CREATED, () ->
                notificationService.sendDomainNotification(
                        event.buyerId(),
                        NotificationChannel.PUSH,
                        null,
                        NotificationTemplateCodeEnum.ORDER_CONFIRMED,
                        Map.of(
                                NotificationTemplateVarEnum.ORDER_ID.getValue(), String.valueOf(event.orderId()),
                                NotificationTemplateVarEnum.TOTAL.getValue(), event.totalAmount().toPlainString(),
                                NotificationTemplateVarEnum.CURRENCY.getValue(), event.currency())));
    }

    @KafkaListener(topics = FkTopics.PAYMENT_SUCCESS, groupId = NotificationServiceConstants.KAFKA_GROUP_DOMAIN)
    public void onPaymentSuccess(PaymentSuccessEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.PAYMENT_SUCCESS, () ->
                notificationService.sendDomainNotification(
                        event.userId(),
                        NotificationChannel.PUSH,
                        null,
                        NotificationTemplateCodeEnum.PAYMENT_RECEIPT,
                        Map.of(
                                NotificationTemplateVarEnum.ORDER_ID.getValue(), String.valueOf(event.orderId()),
                                NotificationTemplateVarEnum.AMOUNT.getValue(), event.amount().toPlainString(),
                                NotificationTemplateVarEnum.CURRENCY.getValue(), event.currency())));
    }

    @KafkaListener(topics = FkTopics.SHIPMENT_DELIVERED, groupId = NotificationServiceConstants.KAFKA_GROUP_DOMAIN)
    public void onShipmentDelivered(ShipmentDeliveredEvent event) {
        eventGuard.runOnce(event.base().eventId(), FkTopics.SHIPMENT_DELIVERED, () ->
                notificationService.sendDomainNotification(
                        null,
                        NotificationChannel.PUSH,
                        null,
                        NotificationTemplateCodeEnum.ORDER_DELIVERED,
                        Map.of(
                                NotificationTemplateVarEnum.ORDER_ID.getValue(), String.valueOf(event.orderId()),
                                NotificationTemplateVarEnum.TRACKING_NUMBER.getValue(), event.trackingNumber())));
    }
}
