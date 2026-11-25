package com.ecommerce.order.messaging.consumer;

import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.enums.OutboxStatus;
import com.ecommerce.order.event.OrderCancellationFailedEvent;
import com.ecommerce.order.event.OrderCreationFailedEvent;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationFailedConsumer {
    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;

    @KafkaListener(
            topics = "order-cancellation-failed",
            groupId = "order-cancellation-failed-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    @Transactional
    public void consume(String payload) {
        try {
            OrderCancellationFailedEvent orderCancellationFailedEvent = objectMapper.readValue(payload, OrderCancellationFailedEvent.class);

            OutboxEvent outboxEvent = outboxEventService.getById(orderCancellationFailedEvent.getEventId());

            if (OutboxStatus.DEAD_LETTER.equals(outboxEvent.getStatus())) {
                return;
            }

            outboxEventService.markAsDeadLettered(orderCancellationFailedEvent.getEventId(), orderCancellationFailedEvent.getReason());
            //notification...
            log.info(
                    "Received OrderCancellationFailedEvent: eventId={}",
                    orderCancellationFailedEvent.getEventId()
            );

        } catch (Exception e) {
            log.error("Failed to process OrderCancellationFailedEvent: {}", payload, e);
            throw new RuntimeException(
                    "Failed to process OrderCancellationFailedEvent", e);
        }
    }
}
