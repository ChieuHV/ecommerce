package com.ecommerce.order.service.impl;

import com.ecommerce.order.dto.request.ProcessEventRequest;
import com.ecommerce.order.entity.ProcessedEvent;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.event.InventoryReservedEvent;
import com.ecommerce.order.exception.BusinessException;
import com.ecommerce.order.mapper.ProcessEventMapper;
import com.ecommerce.order.repository.ProcessEventRepository;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.service.ProcessEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessEventServiceImpl implements ProcessEventService {

    private final ProcessEventRepository processEventRepository;
    private final OrderService orderService;
    private final ProcessEventMapper processEventMapper;

    @Override
    @Transactional
    public void processInventoryReservedEvent(InventoryReservedEvent event) {

        if (event.getEventId() == null || event.getEventId().isBlank()) {
            throw new BusinessException("EVENT_ID_REQUIRED", "Event ID is required");
        }

        if (event.getOrderId() == null || event.getOrderId().isBlank()) {
            throw new BusinessException("ORDER_ID_REQUIRED", "Order ID is required");
        }

        if (processEventRepository.existsById(event.getEventId())) {
            return;
        }

        orderService.updateStatus(event.getOrderId(), OrderStatus.CONFIRMED);

        //notification...

        ProcessEventRequest processEventRequest = ProcessEventRequest.builder()
                .eventId(event.getEventId())
                .eventType("INVENTORY_RESERVED")
                .build();

        ProcessedEvent processedEvent = processEventMapper.toEntity(processEventRequest);

        processEventRepository.save(processedEvent);
    }
}