package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.response.OutboxEventResponse;
import com.ecommerce.product.entity.OutboxEvent;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventMapper {
    public OutboxEventResponse toResponse(OutboxEvent outboxEvent) {
        return OutboxEventResponse.builder()
                .id(outboxEvent.getId())
                .topic(outboxEvent.getTopic())
                .aggregateType(outboxEvent.getAggregateType())
                .aggregateId(outboxEvent.getAggregateId())
                .eventType(outboxEvent.getEventType())
                .payload(outboxEvent.getPayload())
                .status(outboxEvent.getStatus().name())
                .retryCount(outboxEvent.getRetryCount())
                .errorMessage(outboxEvent.getErrorMessage())
                .createdAt(outboxEvent.getCreatedAt())
                .publishedAt(outboxEvent.getPublishedAt())
                .build();
    }
}
