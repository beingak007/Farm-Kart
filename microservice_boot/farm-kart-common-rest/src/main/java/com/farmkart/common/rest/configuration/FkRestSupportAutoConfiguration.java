package com.farmkart.common.rest.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Shared REST beans loaded on every microservice (exception handler, filters, JWT security).
 */
@Configuration
@ComponentScan(basePackages = {
        "com.farmkart.common.rest.exception",
        "com.farmkart.common.rest.filter",
        "com.farmkart.common.rest.advice",
        "com.farmkart.starter.common.security"
})
public class FkRestSupportAutoConfiguration {
}
