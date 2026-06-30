package com.farmkart.starter.common.dto;

/**
 * Stable, machine-readable error codes returned to API clients.
 * UI and integrations should branch on {@code code}, not HTTP status alone.
 */
public enum ApiErrorCode {

    VALIDATION_FAILED("VALIDATION_FAILED", "One or more fields are invalid"),
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "The requested resource was not found"),
    UNAUTHORIZED("UNAUTHORIZED", "Authentication required"),
    FORBIDDEN("FORBIDDEN", "You do not have permission to perform this action"),
    CONFLICT("CONFLICT", "The request conflicts with the current state"),
    RATE_LIMITED("RATE_LIMITED", "Too many requests. Please try again later"),
    BAD_REQUEST("BAD_REQUEST", "The request could not be processed"),
    INTERNAL_ERROR("INTERNAL_ERROR", "Something went wrong. Please try again later"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", "Service temporarily unavailable");

    private final String code;
    private final String defaultMessage;

    ApiErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String code() {
        return code;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
