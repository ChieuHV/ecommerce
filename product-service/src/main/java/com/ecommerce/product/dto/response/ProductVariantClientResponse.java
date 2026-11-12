package com.ecommerce.product.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductVariantClientResponse {
    private String productId;

    private String variantId;

    private String name;

    private String description;

    private String imageUrl;

    private String brand;

    private String sku;

    private String barcode;

    private BigDecimal price;

    private Map<String, String> attributes;
}
