package com.ecommerce.inventory.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private String eventId;

    private String orderId;

    private String orderNumber;

    private LocalDateTime createdAt;

    private List<OrderCreatedItemEvent> items;
}
