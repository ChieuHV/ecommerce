package com.ecommerce.product.messaging.publisher;

import com.ecommerce.product.entity.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<SendResult<String, String>> publish(OutboxEvent event) {
        return kafkaTemplate.send(event.getTopic(), event.getId(), event.getPayload());
    }

    public CompletableFuture<SendResult<String, String>> publishToDlt(OutboxEvent event) {
        String dltTopic = event.getTopic() + ".DLT";

        return kafkaTemplate.send(dltTopic, event.getId(), event.getPayload());
    }
}