package com.farmkart.starter.common.events;

import java.time.Instant;

public record ShipmentDeliveredEvent(
        FkBaseEvent base,
        Long shipmentId,
        Long orderId,
        String trackingNumber,
        Instant deliveredAt
) {}
