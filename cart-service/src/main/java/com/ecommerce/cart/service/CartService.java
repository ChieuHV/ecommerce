package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.request.CartItemRequest;
import com.ecommerce.cart.dto.response.CartResponse;

import java.util.List;

public interface CartService {

    CartResponse save(List<CartItemRequest> cartItemRequestList);

    CartResponse findByUserId();

    void delete(String id);
}