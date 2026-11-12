package com.ecommerce.product.messaging.consumer;

import com.ecommerce.product.event.ProductVariantCreationFailedEvent;
import com.ecommerce.product.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductVariantCreationFailedConsumer {

    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;

    @KafkaListener(
            topics = "product-variant-creation-failed",
            groupId = "product-variant_failed-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    public void consume(String payload) {
        try {
            ProductVariantCreationFailedEvent productVariantCreationFailedEvent = objectMapper.readValue(payload, ProductVariantCreationFailedEvent.class);

            outboxEventService.markAsDeadLettered(productVariantCreationFailedEvent.getEventId(), productVariantCreationFailedEvent.getReason());
            log.info(
                    "Received ProductVariantCreationFailedEvent: eventId={}",
                    productVariantCreationFailedEvent.getEventId()
            );

        } catch (Exception e) {
            log.error("Failed to process ProductVariantCreationFailedEvent: {}", payload, e);
            throw new RuntimeException(
                    "Failed to process ProductVariantCreationFailedEvent", e);
        }
    }
}
