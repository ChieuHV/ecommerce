package com.ecommerce.inventory.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancellationFailedEvent {
    private String eventId;
    private String orderId;
    private String reason;
}
