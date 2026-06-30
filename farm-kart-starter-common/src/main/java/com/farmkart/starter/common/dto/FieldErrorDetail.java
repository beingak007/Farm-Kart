package com.farmkart.starter.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldErrorDetail(
        String field,
        String message,
        String code
) {}
