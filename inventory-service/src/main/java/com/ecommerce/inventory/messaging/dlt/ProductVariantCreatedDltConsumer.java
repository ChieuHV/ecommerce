package com.ecommerce.inventory.messaging.dlt;

import com.ecommerce.inventory.event.ProductVariantCreatedEvent;
import com.ecommerce.inventory.event.ProductVariantCreationFailedEvent;
import com.ecommerce.inventory.messaging.publisher.ProductVariantCreationFailedPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductVariantCreatedDltConsumer {

    private final ObjectMapper objectMapper;
    private final ProductVariantCreationFailedPublisher productCreationFailedPublisher;

    @KafkaListener(
            topics = "product-variant-created.DLT",
            groupId = "inventory-product-variant-created-dlt",
            containerFactory = "manualKafkaListenerContainerFactory"
    )
    public void consume(String payload) {
        try {
            ProductVariantCreatedEvent event = objectMapper.readValue(payload, ProductVariantCreatedEvent.class);

            ProductVariantCreationFailedEvent failedEvent =
                    ProductVariantCreationFailedEvent.builder()
                            .eventId(event.getEventId())
                            .variantId(event.getVariantId())
                            .reason("Failed after retries")
                            .build();

            String failedPayload = objectMapper.writeValueAsString(failedEvent);

            productCreationFailedPublisher.publish(
                    failedEvent.getEventId(),
                    failedPayload
            );

            log.info(
                    "Published ProductCreationFailedEvent: eventId={}, variantId={}",
                    failedEvent.getEventId(),
                    failedEvent.getVariantId()
            );

        } catch (Exception e) {
            log.error("Failed to process DLT message: {}", payload, e);
            throw new RuntimeException("Failed to process DLT message", e);
        }
    }
}