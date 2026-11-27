package com.ecommerce.order.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedEvent {

    private String eventId;

    private String orderId;

    private String orderNumber;

    private LocalDateTime reservedAt;

    private List<InventoryReservedItemEvent> items;
}
