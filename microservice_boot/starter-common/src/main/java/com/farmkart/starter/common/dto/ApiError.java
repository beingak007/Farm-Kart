package com.farmkart.starter.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Structured error payload — safe for UI consumption.
 * Never includes stack traces, SQL, or internal exception messages.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        List<FieldErrorDetail> details
) {

    public static ApiError of(ApiErrorCode errorCode, String message) {
        return new ApiError(errorCode.code(), message, null);
    }

    public static ApiError of(ApiErrorCode errorCode, String message, List<FieldErrorDetail> details) {
        return new ApiError(errorCode.code(), message, details);
    }
}
