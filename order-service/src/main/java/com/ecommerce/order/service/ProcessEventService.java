package com.ecommerce.order.service;

import com.ecommerce.order.event.InventoryReservedEvent;

public interface ProcessEventService {

    void processInventoryReservedEvent (InventoryReservedEvent event);
}