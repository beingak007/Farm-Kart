package com.farmkart.starter.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standard API envelope used across all Farm Kart microservices.
 *
 * <p>Success:
 * <pre>{@code { "success": true, "data": {...}, "meta": { "requestId": "...", "timestamp": "..." } }}</pre>
 *
 * <p>Error (no internal details leaked):
 * <pre>{@code { "success": false, "message": "...", "error": { "code": "...", "message": "...", "details": [...] }, "meta": {...} }}</pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        ApiError error,
        ResponseMeta responseMeta
) {

    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, null);
    }

    public static <T> ApiResponse<T> ok(T data, ResponseMeta meta) {
        return new ApiResponse<>(true, null, data, null, meta);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    public static ApiResponse<Void> okMessage(String message) {
        return new ApiResponse<>(true, message, null, null, null);
    }

    /** @deprecated Prefer {@link #failure(ApiError, ResponseMeta)} */
    @Deprecated
    public static ApiResponse<Void> error(String message) {
        return failure(ApiError.of(ApiErrorCode.BAD_REQUEST, message), null);
    }

    public static ApiResponse<Void> failure(ApiError error, ResponseMeta meta) {
        return new ApiResponse<>(false, error.message(), null, error, meta);
    }
}
