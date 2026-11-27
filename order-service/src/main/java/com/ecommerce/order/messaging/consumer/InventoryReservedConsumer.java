package com.ecommerce.order.messaging.consumer;

import com.ecommerce.order.event.InventoryReservedEvent;
import com.ecommerce.order.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryReservedConsumer {
    private final ObjectMapper objectMapper;
    private final ProcessEventService processEventService;

    @KafkaListener(
            topics = "inventory-reserved",
            groupId = "inventory-reserved-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    public void consume(String payload) {
        try {
            InventoryReservedEvent event = objectMapper.readValue(payload, InventoryReservedEvent.class);
            processEventService.processInventoryReservedEvent(event);

            log.info(
                    "Processed InventoryReservedEvent: eventId={}, variantId={}",
                    event.getEventId(),
                    event.getOrderId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process InventoryReservedEvent: {}",
                    payload,
                    e
            );

            throw new RuntimeException(
                    "Failed to process InventoryReservedEvent",
                    e
            );
        }
    }
}
