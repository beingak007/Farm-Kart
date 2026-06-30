package com.farmkart.starter.common.context;

import com.farmkart.starter.common.dto.ResponseMeta;
import org.slf4j.MDC;

import java.time.Instant;

/**
 * Per-request context propagated via SLF4J MDC (set by {@code RequestIdFilter}).
 */
public final class RequestContext {

    public static final String REQUEST_ID_KEY = "requestId";

    private RequestContext() {}

    public static String getRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }

    public static ResponseMeta currentMeta() {
        return new ResponseMeta(getRequestId(), Instant.now());
    }
}
