package com.ecommerce.inventory.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledEvent {

    private String eventId;

    private String orderId;

    private String orderNumber;

    private String reason;

    private LocalDateTime cancelledAt;

    private List<OrderCancelledItemEvent> items;
}