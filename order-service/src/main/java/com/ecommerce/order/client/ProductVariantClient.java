package com.ecommerce.order.client;

import com.ecommerce.order.common.response.ApiResponse;
import com.ecommerce.order.dto.response.ProductVariantClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductVariantClient {
    @PostMapping("/by-ids")
    ApiResponse<List<ProductVariantClientResponse>> getVariantsByIds(@RequestBody List<String> ids);

    @GetMapping("/{id}")
    ApiResponse<ProductVariantClientResponse> getVariantById(@PathVariable String id);
}
