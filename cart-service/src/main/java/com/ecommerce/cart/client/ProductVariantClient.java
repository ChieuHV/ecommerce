package com.ecommerce.cart.client;

import com.ecommerce.cart.dto.response.ProductVariantClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "product-service", url = "${services.product.url}")
public interface ProductVariantClient {
    @GetMapping("/by-ids")
    List<ProductVariantClientResponse> getVariantsByIds(@RequestParam List<String> ids);
}
