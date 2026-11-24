package com.ecommerce.order.service;

import com.ecommerce.order.dto.request.CancelOrderRequest;
import com.ecommerce.order.dto.request.OrderRequest;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.enums.OrderStatus;

public interface OrderService {
    OrderResponse createOrder(OrderRequest orderRequest);

    Order updateStatus(String orderId, OrderStatus orderStatus);

    OrderResponse cancelOrder(String orderId, CancelOrderRequest request);
}
