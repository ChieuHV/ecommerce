package com.ecommerce.order.messaging.dlt;

import com.ecommerce.order.event.InventoryReservationFailedEvent;
import com.ecommerce.order.event.InventoryReservedEvent;
import com.ecommerce.order.messaging.publisher.InventoryReservationFailedPublisher;
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
public class InventoryReservedDltConsumer {

    private final ObjectMapper objectMapper;
    private final InventoryReservationFailedPublisher inventoryReservationFailedPublisher;

    @KafkaListener(
            topics = "inventory-reserved.DLT",
            groupId = "inventory-reserved-dlt",
            containerFactory = "manualKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            InventoryReservedEvent event = objectMapper.readValue(record.value(), InventoryReservedEvent.class);

            Header header = record.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);

            String errorMessage = header != null ? new String(header.value(), StandardCharsets.UTF_8) : "Failed after retries";

            InventoryReservationFailedEvent failedEvent =
                    InventoryReservationFailedEvent.builder()
                            .eventId(event.getEventId())
                            .orderId(event.getOrderId())
                            .reason(errorMessage)
                            .build();

            String failedPayload = objectMapper.writeValueAsString(failedEvent);

            inventoryReservationFailedPublisher.publish(
                    failedEvent.getEventId(),
                    failedPayload
            );

            log.info(
                    "Published OrderCreationFailedEvent: eventId={}, variantId={}",
                    failedEvent.getEventId(),
                    failedEvent.getOrderId()
            );

        } catch (Exception e) {
            log.error("Failed to process DLT message: {}", record.value(), e);
            throw new RuntimeException("Failed to process DLT message", e);
        }
    }
}
