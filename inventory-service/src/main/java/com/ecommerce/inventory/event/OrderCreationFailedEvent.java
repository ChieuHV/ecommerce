package com.ecommerce.inventory.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreationFailedEvent {
    private String eventId;
    private String orderId;
    private String reason;
}
