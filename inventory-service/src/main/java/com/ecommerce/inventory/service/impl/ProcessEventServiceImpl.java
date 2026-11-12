package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.request.ProcessEventRequest;
import com.ecommerce.inventory.dto.response.ProcessEventResponse;
import com.ecommerce.inventory.entity.ProcessedEvent;
import com.ecommerce.inventory.event.ProductVariantCreatedEvent;
import com.ecommerce.inventory.mapper.ProcessEventMapper;
import com.ecommerce.inventory.repository.ProcessEventRepository;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessEventServiceImpl implements ProcessEventService {

    private final ProcessEventRepository processEventRepository;
    private final ProcessEventMapper processEventMapper;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public ProcessEventResponse save(ProcessEventRequest request) {
        ProcessedEvent processedEvent = processEventMapper.toEntity(request);

        ProcessedEvent savedEvent =
                processEventRepository.save(processedEvent);

        return processEventMapper.toProcessEventResponse(savedEvent);
    }

    @Override
    @Transactional
    public void processProductVariantCreatedEvent(ProductVariantCreatedEvent event) {
        if (processEventRepository.existsById(event.getEventId())) {
            return;
        }

        if (event.getVariantId() == null || event.getVariantId().isBlank()) {
            throw new IllegalArgumentException("Variant ID is required");
        }

        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new IllegalArgumentException("Invalid initial quantity");
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new IllegalArgumentException("Event ID is required");
        }

        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .variantId(event.getVariantId())
                .quantity(event.getQuantity())
                .build();

        inventoryService.save(inventoryRequest);

        ProcessEventRequest processEventRequest = ProcessEventRequest.builder()
                .eventId(event.getEventId())
                .variantId(event.getVariantId())
                .build();

        ProcessedEvent processedEvent = processEventMapper.toEntity(processEventRequest);

        processEventRepository.save(processedEvent);
    }
}