package com.ecommerce.product.dto.response;

import com.ecommerce.product.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;

    private String name;

    private String slug;

    private String description;

    private String brand;

    private String imageUrl;

    private ProductStatus status;

    private Boolean isFeatured;

    private String initBy;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;

    private String categoryId;

    private String categoryName;
}