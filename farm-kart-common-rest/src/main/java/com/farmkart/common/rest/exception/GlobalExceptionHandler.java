package com.farmkart.common.rest.exception;

import com.farmkart.starter.common.context.RequestContext;
import com.farmkart.starter.common.dto.ApiError;
import com.farmkart.starter.common.dto.ApiErrorCode;
import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.starter.common.dto.FieldErrorDetail;
import com.farmkart.starter.common.dto.ResponseMeta;
import com.farmkart.starter.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

/**
 * Central exception handler — returns safe, structured errors to clients.
 * Internal exception details are logged server-side only.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        ApiError error = ApiError.of(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getDetails().isEmpty() ? null : ex.getDetails());
        return respond(ex.getStatus(), error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldError)
                .toList();
        ApiError error = ApiError.of(
                ApiErrorCode.VALIDATION_FAILED,
                ApiErrorCode.VALIDATION_FAILED.defaultMessage(),
                details);
        return respond(HttpStatus.BAD_REQUEST.value(), error);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParam(MissingServletRequestParameterException ex) {
        FieldErrorDetail detail = new FieldErrorDetail(ex.getParameterName(), "Required parameter is missing", "REQUIRED");
        ApiError error = ApiError.of(
                ApiErrorCode.VALIDATION_FAILED,
                ApiErrorCode.VALIDATION_FAILED.defaultMessage(),
                List.of(detail));
        return respond(HttpStatus.BAD_REQUEST.value(), error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        FieldErrorDetail detail = new FieldErrorDetail(
                ex.getName(),
                "Invalid value for parameter",
                "INVALID");
        ApiError error = ApiError.of(
                ApiErrorCode.VALIDATION_FAILED,
                ApiErrorCode.VALIDATION_FAILED.defaultMessage(),
                List.of(detail));
        return respond(HttpStatus.BAD_REQUEST.value(), error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.debug("Malformed request body requestId={}", RequestContext.getRequestId(), ex);
        return respond(HttpStatus.BAD_REQUEST.value(),
                ApiError.of(ApiErrorCode.BAD_REQUEST, "Request body is malformed or missing"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return respond(HttpStatus.METHOD_NOT_ALLOWED.value(),
                ApiError.of(ApiErrorCode.BAD_REQUEST, "HTTP method not supported for this endpoint"));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return respond(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                ApiError.of(ApiErrorCode.BAD_REQUEST, "Unsupported content type"));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NoResourceFoundException ex) {
        return respond(HttpStatus.NOT_FOUND.value(),
                ApiError.of(ApiErrorCode.RESOURCE_NOT_FOUND, ApiErrorCode.RESOURCE_NOT_FOUND.defaultMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied requestId={}", RequestContext.getRequestId());
        return respond(HttpStatus.FORBIDDEN.value(),
                ApiError.of(ApiErrorCode.FORBIDDEN, ApiErrorCode.FORBIDDEN.defaultMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Data integrity violation requestId={}", RequestContext.getRequestId(), ex);
        return respond(HttpStatus.CONFLICT.value(),
                ApiError.of(ApiErrorCode.CONFLICT, "The operation could not be completed due to a data conflict"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception requestId={} method={} path={}",
                RequestContext.getRequestId(), request.getMethod(), request.getRequestURI(), ex);
        return respond(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ApiError.of(ApiErrorCode.INTERNAL_ERROR, ApiErrorCode.INTERNAL_ERROR.defaultMessage()));
    }

    private ResponseEntity<ApiResponse<Void>> respond(int status, ApiError error) {
        ResponseMeta meta = RequestContext.currentMeta();
        return ResponseEntity.status(status).body(ApiResponse.failure(error, meta));
    }

    private FieldErrorDetail toFieldError(FieldError fieldError) {
        return new FieldErrorDetail(
                fieldError.getField(),
                fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                "INVALID");
    }
}
