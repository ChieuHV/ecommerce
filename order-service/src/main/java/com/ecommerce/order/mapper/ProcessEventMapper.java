package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.request.ProcessEventRequest;
import com.ecommerce.order.dto.response.ProcessEventResponse;
import com.ecommerce.order.entity.ProcessedEvent;
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