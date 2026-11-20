package com.ecommerce.order.dto.response;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemResponse {

    private String id;

    private String productId;

    private String variantId;

    private String productName;

    private String variantName;

    private String sku;

    private String imageUrl;

    private BigDecimal unitPrice;

    private Integer quantity;

    private BigDecimal subtotal;
}
