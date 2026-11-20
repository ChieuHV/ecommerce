package com.ecommerce.product.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantCreatedEvent {
    private String eventId;
    private String variantId;
    private String productId;
    private Integer quantity;
}
