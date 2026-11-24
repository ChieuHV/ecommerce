package com.ecommerce.order.messaging.consumer;

import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.enums.OutboxStatus;
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
public class OrderCreationFailedConsumer {
    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;
    private final OrderService orderService;

    @KafkaListener(
            topics = "order-creation-failed",
            groupId = "order_failed-group",
            containerFactory = "kafkaListenerContainerFactory",
            concurrency = "3"
    )
    @Transactional
    public void consume(String payload) {
        try {
            OrderCreationFailedEvent orderCreationFailedEvent = objectMapper.readValue(payload, OrderCreationFailedEvent.class);

            OutboxEvent outboxEvent = outboxEventService.getById(orderCreationFailedEvent.getEventId());

            if (OutboxStatus.DEAD_LETTER.equals(outboxEvent.getStatus())) {
                return;
            }

            outboxEventService.markAsDeadLettered(orderCreationFailedEvent.getEventId(), orderCreationFailedEvent.getReason());
            orderService.updateStatus(orderCreationFailedEvent.getOrderId(), OrderStatus.FAILED);
            //notification...
            log.info(
                    "Received OrderCreationFailedEvent: eventId={}",
                    orderCreationFailedEvent.getEventId()
            );

        } catch (Exception e) {
            log.error("Failed to process OrderCreationFailedEvent: {}", payload, e);
            throw new RuntimeException(
                    "Failed to process OrderCreationFailedEvent", e);
        }
    }
}
