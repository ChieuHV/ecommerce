package com.ecommerce.order.service.impl;

import com.ecommerce.order.dto.response.OutboxEventResponse;
import com.ecommerce.order.entity.OutboxEvent;
import com.ecommerce.order.enums.OutboxStatus;
import com.ecommerce.order.exception.ResourceNotFoundException;
import com.ecommerce.order.mapper.OutboxEventMapper;
import com.ecommerce.order.repository.OutboxEventRepository;
import com.ecommerce.order.service.OutboxEventService;
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
    public OutboxEventResponse save(OutboxEvent outboxEvent) {
        OutboxEvent savedOutboxEvent = outboxEventRepository.save(outboxEvent);

        return outboxEventMapper.toResponse(savedOutboxEvent);
    }

    @Override
    public List<OutboxEventResponse> save(List<OutboxEvent> outboxEvents) {
        List<OutboxEvent> savedOutboxEvents = outboxEventRepository.saveAll(outboxEvents);

        return savedOutboxEvents.stream().map(outboxEventMapper::toResponse).toList();
    }

    @Override
    public List<OutboxEventResponse> getAll() {
        return outboxEventRepository.findAll()
                .stream()
                .map(outboxEventMapper::toResponse)
                .toList();
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
        OutboxEvent event = getEventOrThrow(eventId);

        event.setStatus(OutboxStatus.SENT);
        event.setPublishedAt(LocalDateTime.now());
        event.setErrorMessage(null);
    }

    @Override
    @Transactional
    public void markForRetry(String eventId, String errorMessage) {
        OutboxEvent event = getEventOrThrow(eventId);

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
        OutboxEvent event = getEventOrThrow(eventId);

        event.setStatus(OutboxStatus.DEAD_LETTER);
        event.setErrorMessage(errorMessage);
    }

    private OutboxEvent getEventOrThrow(String eventId) {
        return outboxEventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Outbox event not found: " + eventId
                ));
    }
}
