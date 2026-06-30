package com.farmkart.starter.common.dto;

import java.time.Instant;

/**
 * Response metadata attached to every API envelope (success and error).
 */
public record ResponseMeta(
        String requestId,
        Instant timestamp
) {}
