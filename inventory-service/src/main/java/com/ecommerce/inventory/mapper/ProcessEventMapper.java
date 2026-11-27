package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.dto.request.ProcessEventRequest;
import com.ecommerce.inventory.dto.response.ProcessEventResponse;
import com.ecommerce.inventory.entity.ProcessedEvent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProcessEventMapper {

    public ProcessedEvent toEntity(ProcessEventRequest request) {
        return ProcessedEvent.builder()
                .eventId(request.getEventId())
                .eventType(request.getEventType())
                .processedAt(LocalDateTime.now())
                .build();
    }

    public ProcessEventResponse toProcessEventResponse(ProcessedEvent processedEvent) {
        return ProcessEventResponse.builder()
                .eventId(processedEvent.getEventId())
                .eventType(processedEvent.getEventType())
                .processedAt(processedEvent.getProcessedAt())
                .build();
    }
}