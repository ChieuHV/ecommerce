package com.ecommerce.order.mapper;

import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;

    public OrderResponse toResponse(Order order, List<OrderItem> orderItems) {

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discountAmount(order.getDiscountAmount())
                .totalQuantity(orderItems.stream().mapToInt(OrderItem::getQuantity).sum())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .recipientName(order.getRecipientName())
                .phoneNumber(order.getPhoneNumber())
                .addressLine(order.getAddressLine())
                .ward(order.getWard())
                .district(order.getDistrict())
                .city(order.getCity())
                .country(order.getCountry())
                .postalCode(order.getPostalCode())
                .customerNote(order.getCustomerNote())
                .cancellationReason(order.getCancellationReason())
                .cancelledAt(order.getCancelledAt())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(
                        orderItems.stream()
                                .map(orderItemMapper::toResponse)
                                .toList()
                )
                .build();
    }
}
