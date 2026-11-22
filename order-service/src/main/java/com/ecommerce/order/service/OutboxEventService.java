package com.ecommerce.order.service;

import com.ecommerce.order.dto.response.OutboxEventResponse;
import com.ecommerce.order.entity.OutboxEvent;

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
