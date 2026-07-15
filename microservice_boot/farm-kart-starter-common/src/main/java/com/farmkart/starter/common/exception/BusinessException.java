package com.farmkart.starter.common.exception;

import com.farmkart.starter.common.dto.ApiErrorCode;
import com.farmkart.starter.common.dto.FieldErrorDetail;

import java.util.List;

/**
 * Domain/business fault safe to expose to API clients.
 * Throw this for expected failures (validation, not-found, conflict, auth).
 * Never put stack traces, SQL, or internal system details in the message.
 */
public class BusinessException extends RuntimeException {

    private final int status;
    private final ApiErrorCode errorCode;
    private final List<FieldErrorDetail> details;

    public BusinessException(String message) {
        this(400, ApiErrorCode.BAD_REQUEST, message);
    }

    public BusinessException(int status, String message) {
        this(status, mapStatusToCode(status), message, List.of());
    }

    public BusinessException(ApiErrorCode errorCode, String message) {
        this(mapCodeToStatus(errorCode), errorCode, message, List.of());
    }

    public BusinessException(int status, ApiErrorCode errorCode, String message) {
        this(status, errorCode, message, List.of());
    }

    public BusinessException(int status, ApiErrorCode errorCode, String message,
                             List<FieldErrorDetail> details) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    public int getStatus() {
        return status;
    }

    public ApiErrorCode getErrorCode() {
        return errorCode;
    }

    public List<FieldErrorDetail> getDetails() {
        return details;
    }

    private static ApiErrorCode mapStatusToCode(int status) {
        return switch (status) {
            case 401 -> ApiErrorCode.UNAUTHORIZED;
            case 403 -> ApiErrorCode.FORBIDDEN;
            case 404 -> ApiErrorCode.RESOURCE_NOT_FOUND;
            case 409 -> ApiErrorCode.CONFLICT;
            case 429 -> ApiErrorCode.RATE_LIMITED;
            case 503 -> ApiErrorCode.SERVICE_UNAVAILABLE;
            default -> ApiErrorCode.BAD_REQUEST;
        };
    }

    private static int mapCodeToStatus(ApiErrorCode code) {
        return switch (code) {
            case UNAUTHORIZED -> 401;
            case FORBIDDEN -> 403;
            case RESOURCE_NOT_FOUND -> 404;
            case CONFLICT -> 409;
            case RATE_LIMITED -> 429;
            case SERVICE_UNAVAILABLE -> 503;
            case INTERNAL_ERROR -> 500;
            default -> 400;
        };
    }
}
