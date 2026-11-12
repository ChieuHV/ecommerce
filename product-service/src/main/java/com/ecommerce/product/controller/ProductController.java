package com.ecommerce.product.controller;

import com.ecommerce.product.common.response.ApiResponse;
import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> create(@RequestBody List<@Valid ProductRequest> productRequests) {
        List<ProductResponse> productResponses = productService.create(productRequests);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Products created successfully", productResponses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable String id) {
        ProductResponse category = productService.getById(id);

        return ResponseEntity.ok(ApiResponse.success("Product retrieved successfully", category));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAll() {
        List<ProductResponse> productResponses = productService.getAll();

        return ResponseEntity.ok(ApiResponse.success("Products retrieved successfully", productResponses));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable String id, @Valid @RequestBody ProductRequest request) {
        ProductResponse category = productService.update(id, request);

        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        productService.delete(id);

        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }
}
