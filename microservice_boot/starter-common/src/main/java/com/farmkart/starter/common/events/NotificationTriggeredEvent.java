package com.farmkart.starter.common.events;

import java.util.Map;

public record NotificationTriggeredEvent(
        FkBaseEvent base,
        Long userId,
        String channel,        // SMS | EMAIL | PUSH
        String templateCode,   // e.g. "ORDER_CONFIRMED"
        Map<String, String> templateVars,
        String recipientContact  // phone or email
) {}
