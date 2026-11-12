package com.ecommerce.product.service;

import com.ecommerce.product.dto.request.CategoryRequest;
import com.ecommerce.product.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);

    CategoryResponse getById(String id);

    List<CategoryResponse> getAll();

    CategoryResponse update(String id, CategoryRequest request);

    void delete(String id);
}
