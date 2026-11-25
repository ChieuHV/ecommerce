package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.dto.response.OutboxEventResponse;
import com.ecommerce.inventory.entity.OutboxEvent;
import com.ecommerce.inventory.enums.OutboxStatus;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.mapper.OutboxEventMapper;
import com.ecommerce.inventory.repository.OutboxEventRepository;
import com.ecommerce.inventory.service.OutboxEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxEventServiceImpl implements OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventMapper outboxEventMapper;

    @Value("${outbox.max-attempts:3}")
    private int maxAttempts;

    @Override
    public List<OutboxEventResponse> getAll() {
        return outboxEventRepository.findAll()
                .stream()
                .map(outboxEventMapper::toResponse)
                .toList();
    }

    @Override
    public OutboxEvent getById(String id) {
        return outboxEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Outbox event not found: " + id
                ));
    }

    @Override
    @Transactional
    public List<OutboxEvent> claimPendingEvents(int limit) {
        List<OutboxEvent> events = outboxEventRepository.findPendingEventsForUpdate(limit);

        for (OutboxEvent event : events) {
            event.setStatus(OutboxStatus.PROCESSING);
        }

        return events;
    }

    @Override
    @Transactional
    public void markAsSent(String eventId) {
        OutboxEvent event = getById(eventId);

        event.setStatus(OutboxStatus.SENT);
        event.setPublishedAt(LocalDateTime.now());
        event.setErrorMessage(null);
    }

    @Override
    @Transactional
    public void markForRetry(String eventId, String errorMessage) {
        OutboxEvent event = getById(eventId);

        int retryCount = event.getRetryCount() + 1;
        event.setRetryCount(retryCount);
        event.setErrorMessage(errorMessage);

        if (retryCount >= maxAttempts) {
            event.setStatus(OutboxStatus.FAILED);
        } else {
            event.setStatus(OutboxStatus.NEW);
        }
    }

    @Override
    @Transactional
    public void markAsDeadLettered(String eventId, String errorMessage) {
        OutboxEvent event = getById(eventId);

        event.setStatus(OutboxStatus.DEAD_LETTER);
        event.setErrorMessage(errorMessage);
    }
}
