package com.ecommerce.inventory.messaging.consumer;

import com.ecommerce.inventory.event.ProductVariantCreatedEvent;
import com.ecommerce.inventory.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductVariantCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final ProcessEventService processEventService;

    @KafkaListener(
            topics = "product-variant-created",
            groupId = "inventory-product-variant-created",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    public void consume(String payload) {
        try {
            ProductVariantCreatedEvent event = objectMapper.readValue(payload, ProductVariantCreatedEvent.class);
            processEventService.processProductVariantCreatedEvent(event);

            log.info(
                    "Processed ProductVariantCreatedEvent: eventId={}, variantId={}",
                    event.getEventId(),
                    event.getVariantId()
            );

        } catch (Exception e) {
            log.error(
                    "Failed to process ProductVariantCreatedEvent: {}",
                    payload,
                    e
            );

            throw new RuntimeException(
                    "Failed to process ProductVariantCreatedEvent",
                    e
            );
        }
    }
}