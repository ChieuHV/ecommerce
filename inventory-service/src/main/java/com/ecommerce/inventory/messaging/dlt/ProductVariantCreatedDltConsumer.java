package com.ecommerce.inventory.messaging.dlt;

import com.ecommerce.inventory.event.ProductVariantCreatedEvent;
import com.ecommerce.inventory.event.ProductVariantCreationFailedEvent;
import com.ecommerce.inventory.messaging.publisher.ProductVariantCreationFailedPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

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
    public void consume(ConsumerRecord<String, String> record) {
        try {
            ProductVariantCreatedEvent event = objectMapper.readValue(record.value(), ProductVariantCreatedEvent.class);

            Header header = record.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);

            String errorMessage = header != null ? new String(header.value(), StandardCharsets.UTF_8) : "Failed after retries";

            ProductVariantCreationFailedEvent failedEvent =
                    ProductVariantCreationFailedEvent.builder()
                            .eventId(event.getEventId())
                            .variantId(event.getVariantId())
                            .reason(errorMessage)
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
            log.error("Failed to process DLT message: {}", record.value(), e);
            throw new RuntimeException("Failed to process DLT message", e);
        }
    }
}