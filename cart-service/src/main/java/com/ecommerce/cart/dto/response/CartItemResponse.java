package com.ecommerce.cart.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartItemResponse {

    private String id;

    private String variantId;

    private String productId;

    private String productName;

    private String imageUrl;

    private String sku;

    private String barcode;

    private Map<String, String> attributes;

    private BigDecimal price;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}