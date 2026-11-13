package com.ecommerce.cart.controller;

import com.ecommerce.cart.common.response.ApiResponse;
import com.ecommerce.cart.dto.request.CartItemRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;
import com.ecommerce.cart.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        cartItemService.delete(id);

        return ResponseEntity.ok(ApiResponse.success("Cart Item deleted successfully", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CartItemResponse>> update(@PathVariable String id, @Valid @RequestBody CartItemRequest cartItemRequest) {
        CartItemResponse updatedCartItem = cartItemService.update(id, cartItemRequest);

        return ResponseEntity.ok(ApiResponse.success("Cart Item updated successfully", updatedCartItem));
    }
}
