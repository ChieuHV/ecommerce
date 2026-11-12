package com.ecommerce.product.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {
    private String id;

    private String sku;

    private String barcode;

    private BigDecimal price;

    private Map<String, String> attributes;

    private String productId;
}
