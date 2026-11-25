package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.response.OutboxEventResponse;
import com.ecommerce.inventory.entity.OutboxEvent;

import java.util.List;

public interface OutboxEventService {

    List<OutboxEventResponse> getAll();

    OutboxEvent getById(String id);

    List<OutboxEvent> claimPendingEvents(int limit);

    void markAsSent(String eventId);

    void markForRetry(String eventId, String errorMessage);

    void markAsDeadLettered(String eventId, String errorMessage);
}
