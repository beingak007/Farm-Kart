package com.farmkart.service.kafka;

import com.farmkart.service.kafka.event.SheetUploadedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SheetUploadProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public SheetUploadProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${farmkart.kafka.topics.sheet-uploaded:farmkart.sheet.uploaded}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(SheetUploadedEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.uploadId()), event);
    }
}
