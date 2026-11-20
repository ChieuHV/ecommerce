package com.ecommerce.cart.client;

import com.ecommerce.cart.common.response.ApiResponse;
import com.ecommerce.cart.dto.response.ProductVariantClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "product-service", url = "${services.product-variants.url}")
public interface ProductVariantClient {
    @PostMapping("/by-ids")
    ApiResponse<List<ProductVariantClientResponse>> getVariantsByIds(@RequestBody List<String> ids);

    @GetMapping("/{id}")
    ApiResponse<ProductVariantClientResponse> getVariantById(@PathVariable String id);
}
