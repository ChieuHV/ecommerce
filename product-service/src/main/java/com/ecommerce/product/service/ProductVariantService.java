package com.ecommerce.product.service;

import com.ecommerce.product.dto.request.ProductVariantRequest;
import com.ecommerce.product.dto.response.ProductVariantClientResponse;
import com.ecommerce.product.dto.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {
    List<ProductVariantResponse> create(List<ProductVariantRequest> productVariantRequest);

    ProductVariantResponse update(String id, ProductVariantRequest productVariantRequest);

    List<ProductVariantResponse> findByProduct(String productId);

    void delete(String id);

    void deleteByProduct(String productId);

    List<ProductVariantClientResponse> getByIds(List<String> variantIds);

    ProductVariantClientResponse getById(String variantId);
}
