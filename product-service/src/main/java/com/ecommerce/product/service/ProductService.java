package com.ecommerce.product.service;

import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> create(List<ProductRequest> productRequests);

    ProductResponse getById(String id);

    List<ProductResponse> getAll();

    ProductResponse update(String id, ProductRequest productRequest);

    void delete(String id);
}
