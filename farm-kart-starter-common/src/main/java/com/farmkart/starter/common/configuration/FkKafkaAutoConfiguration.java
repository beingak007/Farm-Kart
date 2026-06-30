package com.farmkart.starter.common.configuration;

import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.EventProcessingGuard;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@AutoConfiguration
@ConditionalOnClass(KafkaTemplate.class)
public class FkKafkaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    DomainEventPublisher domainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        return new DomainEventPublisher(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    EventProcessingGuard eventProcessingGuard() {
        return new EventProcessingGuard();
    }
}
