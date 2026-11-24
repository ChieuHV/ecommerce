package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.request.ProcessEventRequest;
import com.ecommerce.inventory.dto.response.ProcessEventResponse;
import com.ecommerce.inventory.entity.ProcessedEvent;
import com.ecommerce.inventory.event.OrderCreatedEvent;
import com.ecommerce.inventory.event.OrderCreatedItemEvent;
import com.ecommerce.inventory.event.ProductVariantCreatedEvent;
import com.ecommerce.inventory.exception.BusinessException;
import com.ecommerce.inventory.mapper.ProcessEventMapper;
import com.ecommerce.inventory.repository.ProcessEventRepository;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        ProcessedEvent savedEvent = processEventRepository.save(processedEvent);

        return processEventMapper.toProcessEventResponse(savedEvent);
    }

    @Override
    @Transactional
    public void processProductVariantCreatedEvent(ProductVariantCreatedEvent event) {
        if (processEventRepository.existsById(event.getEventId())) {
            return;
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new BusinessException("EVENT_ID_REQUIRED", "Event ID is required");
        }

        if (event.getVariantId() == null || event.getVariantId().isBlank()) {
            throw new BusinessException("VARIANT_ID_REQUIRED", "Variant ID is required");
        }

        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new BusinessException("INVALID_INITIAL_QUANTITY", "Invalid initial quantity");
        }

        InventoryRequest inventoryRequest = InventoryRequest.builder()
                .variantId(event.getVariantId())
                .quantity(event.getQuantity())
                .build();

        inventoryService.save(inventoryRequest);

        ProcessEventRequest processEventRequest = ProcessEventRequest.builder()
                .eventId(event.getEventId())
                .eventType("STOCK_IMPORTED")
                .build();

        ProcessedEvent processedEvent = processEventMapper.toEntity(processEventRequest);

        processEventRepository.save(processedEvent);
    }

    @Override
    @Transactional
    public void processOrderCreatedEvent(OrderCreatedEvent event) {
        if (processEventRepository.existsById(event.getEventId())) {
            return;
        }

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new BusinessException("EVENT_ID_REQUIRED", "Event ID is required");
        }

        if (event.getOrderId() == null || event.getOrderId().isBlank()) {
            throw new BusinessException("ORDER_ID_REQUIRED", "Order ID is required");
        }

        if (event.getItems() == null || event.getItems().isEmpty()) {
            throw new BusinessException("ORDER_ITEM_REQUIRED", "Order Item is required");
        }

        List<InventoryRequest> inventoryRequests = new ArrayList<>();

        for (OrderCreatedItemEvent orderItem : event.getItems()) {
            if (orderItem.getVariantId() == null || orderItem.getVariantId().isBlank()) {
                throw new BusinessException("VARIANT_ID_REQUIRED", "Variant ID is required");
            }

            if (orderItem.getQuantity() == null || orderItem.getQuantity() < 0) {
                throw new BusinessException("INVALID_INITIAL_QUANTITY", "Invalid initial quantity");
            }

            InventoryRequest inventoryRequest = InventoryRequest.builder()
                    .variantId(orderItem.getVariantId())
                    .quantity(orderItem.getQuantity())
                    .build();

            inventoryRequests.add(inventoryRequest);
        }

        inventoryService.reserveInventories(inventoryRequests);

        ProcessEventRequest processEventRequest = ProcessEventRequest.builder()
                .eventId(event.getEventId())
                .eventType("ORDER_CREATED")
                .build();

        ProcessedEvent processedEvent = processEventMapper.toEntity(processEventRequest);

        processEventRepository.save(processedEvent);
    }
}