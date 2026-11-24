package com.ecommerce.inventory.messaging.consumer;

import com.ecommerce.inventory.event.OrderCreatedEvent;
import com.ecommerce.inventory.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedConsumer {
    private final ObjectMapper objectMapper;
    private final ProcessEventService processEventService;

    @KafkaListener(
            topics = "order-created",
            groupId = "order-created-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    public void consume(String payload) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(payload, OrderCreatedEvent.class);
            processEventService.processOrderCreatedEvent(event);

            log.info(
                    "Processed OrderCreatedEvent: eventId={}, variantId={}",
                    event.getEventId(),
                    event.getOrderId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process OrderCreatedEvent: {}",
                    payload,
                    e
            );

            throw new RuntimeException(
                    "Failed to process OrderCreatedEvent",
                    e
            );
        }
    }
}
