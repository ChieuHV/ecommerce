package com.ecommerce.order.dto.response;

import com.ecommerce.order.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderResponse {
    private String id;

    private String orderNumber;

    private BigDecimal subtotal;

    private BigDecimal shippingFee;

    private BigDecimal discountAmount;

    private Integer totalQuantity;

    private BigDecimal totalAmount;

    private String currency;

    private String recipientName;

    private String phoneNumber;

    private String addressLine;

    private String ward;

    private String district;

    private String city;

    private String country;

    private String postalCode;

    private String customerNote;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;
}
