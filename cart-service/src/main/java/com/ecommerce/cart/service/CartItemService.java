package com.ecommerce.cart.service;

import com.ecommerce.cart.dto.request.CartItemRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;

public interface CartItemService {

    CartItemResponse update(String id, CartItemRequest cartItemRequest);

    void delete(String id);
}
