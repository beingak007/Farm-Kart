package com.farmkart.starter.common.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.farmkart.starter.common.sms.SmsGateway;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FarmKartProperties.class)
public class FarmKartCommonAutoConfiguration {

    @Bean
    SmsGateway smsGateway(FarmKartProperties properties, ObjectMapper objectMapper) {
        return new SmsGateway(properties, objectMapper);
    }
}
