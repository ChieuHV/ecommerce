package com.ecommerce.order.controller;

import com.ecommerce.order.common.response.ApiResponse;
import com.ecommerce.order.dto.request.CancelOrderRequest;
import com.ecommerce.order.dto.request.OrderRequest;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(@Valid @RequestBody OrderRequest orderRequest) {

        OrderResponse order = orderService.createOrder(orderRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Order created successfully", order)
        );
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> cancel(@PathVariable String id, @Valid @RequestBody CancelOrderRequest request) {

        OrderResponse order = orderService.cancelOrder(id, request);

        return ResponseEntity.ok(
                ApiResponse.success("Order cancelled successfully", order)
        );
    }
}
