package com.ecommerce.cart.controller;

import com.ecommerce.cart.common.response.ApiResponse;
import com.ecommerce.cart.dto.request.CartItemRequest;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<ApiResponse<CartResponse>> create(@RequestBody List<@Valid CartItemRequest> cartItemRequests) {
        CartResponse cartResponse = cartService.save(cartItemRequests);

        return ResponseEntity.ok(ApiResponse.success("Added to cart successfully", cartResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> findByUserId() {
        CartResponse cartResponse = cartService.findByUserId();

        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", cartResponse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        cartService.delete(id);

        return ResponseEntity.ok(ApiResponse.success("Cart deleted successfully", null));
    }
}
