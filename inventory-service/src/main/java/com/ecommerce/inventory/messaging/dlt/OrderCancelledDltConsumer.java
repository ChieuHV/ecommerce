package com.ecommerce.inventory.messaging.dlt;

import com.ecommerce.inventory.event.OrderCancellationFailedEvent;
import com.ecommerce.inventory.event.OrderCancelledEvent;
import com.ecommerce.inventory.messaging.publisher.OrderCancellationFailedPublisher;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancelledDltConsumer {
    private final ObjectMapper objectMapper;
    private final OrderCancellationFailedPublisher orderCancellationFailedPublisher;

    @KafkaListener(
            topics = "order-cancelled.DLT",
            groupId = "order-cancelled-dlt",
            containerFactory = "manualKafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, String> record) {
        try {
            OrderCancelledEvent event = objectMapper.readValue(record.value(), OrderCancelledEvent.class);

            Header header = record.headers().lastHeader(KafkaHeaders.DLT_EXCEPTION_MESSAGE);

            String errorMessage = header != null ? new String(header.value(), StandardCharsets.UTF_8) : "Failed after retries";

            OrderCancellationFailedEvent failedEvent =
                    OrderCancellationFailedEvent.builder()
                            .eventId(event.getEventId())
                            .orderId(event.getOrderId())
                            .reason(errorMessage)
                            .build();

            String failedPayload = objectMapper.writeValueAsString(failedEvent);

            orderCancellationFailedPublisher.publish(
                    failedEvent.getEventId(),
                    failedPayload
            );

            log.info(
                    "Published OrderCancellationFailedEvent: eventId={}, variantId={}",
                    failedEvent.getEventId(),
                    failedEvent.getOrderId()
            );

        } catch (Exception e) {
            log.error("Failed to process DLT message: {}", record.value(), e);
            throw new RuntimeException("Failed to process DLT message", e);
        }
    }
}
