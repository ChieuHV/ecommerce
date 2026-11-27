package com.ecommerce.inventory.messaging.dlt;

import com.ecommerce.inventory.event.OrderCreatedEvent;
import com.ecommerce.inventory.event.OrderCreationFailedEvent;
import com.ecommerce.inventory.messaging.publisher.OrderCreationFailedPublisher;
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
public class OrderCreatedDltConsumer {
    private final ObjectMapper objectMapper;
    private final OrderCreationFailedPublisher orderCreationFailedPublisher;

    @KafkaListener(
            topics = "order-created.DLT",
            groupId = "order-created-dlt",
            containerFactory = "manualKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            OrderCreatedEvent event = objectMapper.readValue(record.value(), OrderCreatedEvent.class);

            Header header = record.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);

            String errorMessage = header != null ? new String(header.value(), StandardCharsets.UTF_8) : "Failed after retries";

            OrderCreationFailedEvent failedEvent =
                    OrderCreationFailedEvent.builder()
                            .eventId(event.getEventId())
                            .orderId(event.getOrderId())
                            .reason(errorMessage)
                            .build();

            String failedPayload = objectMapper.writeValueAsString(failedEvent);

            orderCreationFailedPublisher.publish(
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
