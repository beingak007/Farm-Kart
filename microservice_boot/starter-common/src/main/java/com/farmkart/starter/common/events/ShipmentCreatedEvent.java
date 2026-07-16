package com.farmkart.starter.common.events;

import java.time.Instant;

public record ShipmentCreatedEvent(
        FkBaseEvent base,
        Long shipmentId,
        Long orderId,
        Long logisticsPartnerId,
        String trackingNumber,
        String pickupAddress,
        String deliveryAddress,
        Instant expectedDelivery
) {}
