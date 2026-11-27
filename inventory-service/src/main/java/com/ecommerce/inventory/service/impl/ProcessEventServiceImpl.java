package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.request.ProcessEventRequest;
import com.ecommerce.inventory.dto.response.ProcessEventResponse;
import com.ecommerce.inventory.entity.OutboxEvent;
import com.ecommerce.inventory.entity.ProcessedEvent;
import com.ecommerce.inventory.enums.OutboxStatus;
import com.ecommerce.inventory.event.*;
import com.ecommerce.inventory.exception.BusinessException;
import com.ecommerce.inventory.mapper.ProcessEventMapper;
import com.ecommerce.inventory.repository.OutboxEventRepository;
import com.ecommerce.inventory.repository.ProcessEventRepository;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessEventServiceImpl implements ProcessEventService {

    private final ProcessEventRepository processEventRepository;
    private final ProcessEventMapper processEventMapper;
    private final InventoryService inventoryService;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

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

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new BusinessException("EVENT_ID_REQUIRED", "Event ID is required");
        }

        if (event.getVariantId() == null || event.getVariantId().isBlank()) {
            throw new BusinessException("VARIANT_ID_REQUIRED", "Variant ID is required");
        }

        if (event.getQuantity() == null || event.getQuantity() < 0) {
            throw new BusinessException("INVALID_INITIAL_QUANTITY", "Invalid initial quantity");
        }

        if (processEventRepository.existsById(event.getEventId())) {
            return;
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

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new BusinessException("EVENT_ID_REQUIRED", "Event ID is required");
        }

        if (event.getOrderId() == null || event.getOrderId().isBlank()) {
            throw new BusinessException("ORDER_ID_REQUIRED", "Order ID is required");
        }

        if (event.getItems() == null || event.getItems().isEmpty()) {
            throw new BusinessException("ORDER_ITEM_REQUIRED", "Order Item is required");
        }

        if (processEventRepository.existsById(event.getEventId())) {
            return;
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

        OutboxEvent outboxEvent = buildInventoryReservedOutboxEvent(event);

        outboxEventRepository.save(outboxEvent);

        ProcessEventRequest processEventRequest = ProcessEventRequest.builder()
                .eventId(event.getEventId())
                .eventType("ORDER_CREATED")
                .build();

        ProcessedEvent processedEvent = processEventMapper.toEntity(processEventRequest);

        processEventRepository.save(processedEvent);
    }

    @Override
    public void processOrderCancelledEvent(OrderCancelledEvent event) {

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new BusinessException("EVENT_ID_REQUIRED", "Event ID is required");
        }

        if (event.getOrderId() == null || event.getOrderId().isBlank()) {
            throw new BusinessException("ORDER_ID_REQUIRED", "Order ID is required");
        }

        if (event.getItems() == null || event.getItems().isEmpty()) {
            throw new BusinessException("ORDER_ITEM_REQUIRED", "Order Item is required");
        }

        if (processEventRepository.existsById(event.getEventId())) {
            return;
        }

        List<InventoryRequest> inventoryRequests = new ArrayList<>();

        for (OrderCancelledItemEvent orderItem : event.getItems()) {
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

        inventoryService.releaseReservedInventories(inventoryRequests);

        ProcessEventRequest processEventRequest = ProcessEventRequest.builder()
                .eventId(event.getEventId())
                .eventType("ORDER_CREATED")
                .build();

        ProcessedEvent processedEvent = processEventMapper.toEntity(processEventRequest);

        processEventRepository.save(processedEvent);
    }

    private OutboxEvent buildInventoryReservedOutboxEvent(OrderCreatedEvent orderEvent) {

        InventoryReservedEvent event = InventoryReservedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .orderId(orderEvent.getOrderId())
                .orderNumber(orderEvent.getOrderNumber())
                .reservedAt(LocalDateTime.now(ZoneId.systemDefault()))
                .items(orderEvent.getItems().stream()
                        .map(item -> InventoryReservedItemEvent.builder()
                                .variantId(item.getVariantId())
                                .quantity(item.getQuantity())
                                .build())
                        .toList())
                .build();

        String payload;

        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (Exception exception) {
            throw new BusinessException(
                    "EVENT_SERIALIZATION_FAILED",
                    "Unable to serialize inventory reserved event"
            );
        }

        return OutboxEvent.builder()
                .id(event.getEventId())
                .topic("inventory-reserved")
                .aggregateType("ORDER")
                .aggregateId(orderEvent.getOrderId())
                .eventType("INVENTORY_RESERVED")
                .payload(payload)
                .status(OutboxStatus.NEW)
                .retryCount(0)
                .createdAt(LocalDateTime.now())
                .build();
    }
}