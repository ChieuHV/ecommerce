package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.util.SlugUtil;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public Product toEntity(ProductRequest productRequest) {
        return Product.builder()
                .name(productRequest.getName())
                .slug(SlugUtil.toSlug(productRequest.getName()))
                .description(productRequest.getDescription())
                .brand(productRequest.getBrand())
                .imageUrl(productRequest.getImageUrl())
                .status(productRequest.getStatus())
                .isFeatured(productRequest.getIsFeatured())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .status(product.getStatus())
                .isFeatured(product.getIsFeatured())
                .initBy(product.getInitBy())
                .updateBy(product.getUpdateBy())
                .createdDate(product.getCreatedDate())
                .updatedDate(product.getUpdatedDate())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }
}
