package com.ecommerce.order.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCancelledItemEvent {

    private String variantId;

    private Integer quantity;
}