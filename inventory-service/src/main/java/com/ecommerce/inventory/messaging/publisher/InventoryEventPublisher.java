package com.ecommerce.inventory.messaging.publisher;

import com.ecommerce.inventory.entity.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class InventoryEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CompletableFuture<SendResult<String, String>> publish(OutboxEvent event) {
        return kafkaTemplate.send(event.getTopic(), event.getId(), event.getPayload());
    }
}