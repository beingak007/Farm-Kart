package com.farmkart.common.rest.advice;

import com.farmkart.starter.common.context.RequestContext;
import com.farmkart.starter.common.dto.ApiResponse;
import com.farmkart.starter.common.dto.ResponseMeta;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Ensures every {@link ApiResponse} includes correlation metadata.
 */
@ControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return ApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!(body instanceof ApiResponse<?> api) || api.meta() != null) {
            return body;
        }
        ResponseMeta meta = RequestContext.currentMeta();
        return new ApiResponse<>(api.success(), api.message(), api.data(), api.error(), meta);
    }
}
