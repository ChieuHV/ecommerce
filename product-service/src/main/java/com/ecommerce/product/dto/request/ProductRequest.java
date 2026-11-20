package com.ecommerce.product.dto.request;

import com.ecommerce.product.enums.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200, message = "Product name must not exceed 200 characters")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Product status is required")
    private ProductStatus status;

    @NotNull(message = "Featured status is required")
    private Boolean isFeatured;

    @NotBlank(message = "Category ID is required")
    private String categoryId;
}