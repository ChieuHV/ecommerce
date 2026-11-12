package com.ecommerce.product.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantCreationFailedEvent {

    private String eventId;
    private String variantId;
    private String reason;
}