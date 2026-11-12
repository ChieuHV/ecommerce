package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.request.ProcessEventRequest;
import com.ecommerce.inventory.dto.response.ProcessEventResponse;
import com.ecommerce.inventory.event.ProductVariantCreatedEvent;

public interface ProcessEventService {

    ProcessEventResponse save(ProcessEventRequest processEventRequest);

    void processProductVariantCreatedEvent(
            ProductVariantCreatedEvent event
    );
}