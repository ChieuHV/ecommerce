package com.ecommerce.inventory.messaging.consumer;

import com.ecommerce.inventory.entity.OutboxEvent;
import com.ecommerce.inventory.enums.OutboxStatus;
import com.ecommerce.inventory.event.InventoryReservationFailedEvent;
import com.ecommerce.inventory.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationFailedConsumer {

    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;

    @KafkaListener(
            topics = "inventory-reservation-failed",
            groupId = "inventory-reservation-failed-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    @Transactional
    public void consume(String payload) {
        try {
            InventoryReservationFailedEvent inventoryReservationFailedEvent = objectMapper.readValue(payload, InventoryReservationFailedEvent.class);

            OutboxEvent outboxEvent = outboxEventService.getById(inventoryReservationFailedEvent.getEventId());

            if (OutboxStatus.DEAD_LETTER.equals(outboxEvent.getStatus())) {
                return;
            }

            outboxEventService.markAsDeadLettered(inventoryReservationFailedEvent.getEventId(), inventoryReservationFailedEvent.getReason());

            //notification...
            log.info(
                    "Received InventoryReservationFailedEvent: eventId={}",
                    inventoryReservationFailedEvent.getEventId()
            );

        } catch (Exception e) {
            log.error("Failed to process InventoryReservationFailedEvent: {}", payload, e);
            throw new RuntimeException(
                    "Failed to process InventoryReservationFailedEvent", e);
        }
    }
}
