package com.ecommerce.order.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReservedItemEvent {

    private String variantId;

    private Integer quantity;
}
