package com.ecommerce.inventory.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class OrderCancellationFailedPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "order-cancellation-failed";

    public CompletableFuture<SendResult<String, String>> publish(String eventId, String payload) {
        return kafkaTemplate.send(TOPIC, eventId, payload);
    }
}
