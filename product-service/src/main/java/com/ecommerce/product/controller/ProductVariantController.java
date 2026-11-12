package com.ecommerce.product.controller;

import com.ecommerce.product.common.response.ApiResponse;
import com.ecommerce.product.dto.request.ProductVariantRequest;
import com.ecommerce.product.dto.response.ProductVariantClientResponse;
import com.ecommerce.product.dto.response.ProductVariantResponse;
import com.ecommerce.product.service.ProductVariantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> create(@RequestBody List<@Valid ProductVariantRequest> requests) {

        List<ProductVariantResponse> variants = productVariantService.create(requests);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Product variants created successfully", variants));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> update(@PathVariable String id, @Valid @RequestBody ProductVariantRequest request) {

        ProductVariantResponse variant = productVariantService.update(id, request);

        return ResponseEntity.ok(ApiResponse.success("Product variant updated successfully", variant));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductVariantResponse>>> findByProduct(@PathVariable String productId) {

        List<ProductVariantResponse> variants = productVariantService.findByProduct(productId);

        return ResponseEntity.ok(ApiResponse.success("Product variants retrieved successfully", variants));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {

        productVariantService.delete(id);

        return ResponseEntity.ok(ApiResponse.success("Product variant deleted successfully", null));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteByProduct(@PathVariable String productId) {

        productVariantService.deleteByProduct(productId);

        return ResponseEntity.ok(ApiResponse.success("Product variants deleted successfully", null));
    }

    @GetMapping("/by-ids")
    public ResponseEntity<ApiResponse<List<ProductVariantClientResponse>>> findByIds(@RequestParam List<String> variantIds) {
        List<ProductVariantClientResponse> variants = productVariantService.getByIds(variantIds);

        return ResponseEntity.ok(ApiResponse.success("Product variants retrieved successfully", variants));
    }
}