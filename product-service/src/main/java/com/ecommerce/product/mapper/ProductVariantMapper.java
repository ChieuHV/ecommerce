package com.ecommerce.product.mapper;

import com.ecommerce.product.dto.request.ProductVariantRequest;
import com.ecommerce.product.dto.response.ProductVariantClientResponse;
import com.ecommerce.product.dto.response.ProductVariantResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductVariant;
import org.springframework.stereotype.Component;

@Component
public class ProductVariantMapper {
    public ProductVariant toEntity(ProductVariantRequest productVariantRequest, Product product) {
        return ProductVariant.builder()
                .sku(productVariantRequest.getSku())
                .barcode(productVariantRequest.getBarcode())
                .price(productVariantRequest.getPrice())
                .attributes(productVariantRequest.getAttributes())
                .product(product)
                .build();
    }

    public ProductVariantResponse toResponse(ProductVariant productVariant) {
        return ProductVariantResponse.builder()
                .id(productVariant.getId())
                .sku(productVariant.getSku())
                .barcode(productVariant.getBarcode())
                .price(productVariant.getPrice())
                .attributes(productVariant.getAttributes())
                .productId(productVariant.getProduct().getId())
                .build();
    }

    public ProductVariantClientResponse toClientResponse(ProductVariant variant) {
        Product product = variant.getProduct();

        return ProductVariantClientResponse.builder()
                .productId(product.getId())
                .variantId(variant.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .brand(product.getBrand())
                .sku(variant.getSku())
                .barcode(variant.getBarcode())
                .price(variant.getPrice())
                .attributes(variant.getAttributes())
                .build();
    }
}
