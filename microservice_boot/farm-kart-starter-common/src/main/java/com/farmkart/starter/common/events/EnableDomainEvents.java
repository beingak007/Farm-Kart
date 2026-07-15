package com.farmkart.starter.common.events;

import org.springframework.kafka.annotation.EnableKafka;

/**
 * Marker for services that consume Kafka domain events.
 * Add {@code @Import(FkKafkaConsumerConfiguration.class)} on the application class,
 * or rely on {@link com.farmkart.starter.common.configuration.FkKafkaAutoConfiguration}.
 */
@EnableKafka
public @interface EnableDomainEvents {
}
