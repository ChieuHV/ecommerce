package com.ecommerce.product.service;

import com.ecommerce.product.dto.response.OutboxEventResponse;
import com.ecommerce.product.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventService {
    OutboxEventResponse save(OutboxEvent outboxEvent);

    List<OutboxEventResponse> save(List<OutboxEvent> outboxEvents);

    List<OutboxEventResponse> getAll();

    List<OutboxEvent> claimPendingEvents(int limit);

    void markAsSent(String eventId);

    void markForRetry(String eventId, String errorMessage);

    void markAsDeadLettered(String eventId, String errorMessage);
}
