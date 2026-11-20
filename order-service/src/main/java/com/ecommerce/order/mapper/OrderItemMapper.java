package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.response.OrderItemResponse;
import com.ecommerce.order.entity.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItemResponse toResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProductId())
                .variantId(orderItem.getVariantId())
                .productName(orderItem.getProductName())
                .variantName(orderItem.getVariantName())
                .sku(orderItem.getSku())
                .imageUrl(orderItem.getImageUrl())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}