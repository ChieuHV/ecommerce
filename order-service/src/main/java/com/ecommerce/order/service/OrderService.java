package com.ecommerce.order.service;

import com.ecommerce.order.dto.request.OrderRequest;
import com.ecommerce.order.dto.response.OrderResponse;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);
}
