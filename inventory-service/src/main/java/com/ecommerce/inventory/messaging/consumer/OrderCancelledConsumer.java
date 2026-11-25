package com.ecommerce.inventory.messaging.consumer;

import com.ecommerce.inventory.event.OrderCancelledEvent;
import com.ecommerce.inventory.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledConsumer {
    private final ObjectMapper objectMapper;
    private final ProcessEventService processEventService;

    @KafkaListener(
            topics = "order-cancelled",
            groupId = "order-cancelled-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    public void consume(String payload) {
        try {
            OrderCancelledEvent event = objectMapper.readValue(payload, OrderCancelledEvent.class);
            processEventService.processOrderCancelledEvent(event);

            log.info(
                    "Processed OrderCancelledEvent: eventId={}, variantId={}",
                    event.getEventId(),
                    event.getOrderId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process OrderCancelledEvent: {}",
                    payload,
                    e
            );

            throw new RuntimeException(
                    "Failed to process OrderCancelledEvent",
                    e
            );
        }
    }
}
