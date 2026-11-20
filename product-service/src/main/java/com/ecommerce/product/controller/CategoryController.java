package com.ecommerce.product.controller;

import com.ecommerce.product.common.response.ApiResponse;
import com.ecommerce.product.dto.request.CategoryRequest;
import com.ecommerce.product.dto.response.CategoryResponse;
import com.ecommerce.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor

public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse category = categoryService.create(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Category created successfully", category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable String id) {
        CategoryResponse category = categoryService.getById(id);

        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        List<CategoryResponse> categories = categoryService.getAll();

        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable String id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.update(id, request);

        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        categoryService.delete(id);

        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
