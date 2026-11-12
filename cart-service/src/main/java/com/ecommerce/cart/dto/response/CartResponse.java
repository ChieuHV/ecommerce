package com.ecommerce.cart.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CartResponse {

    private String id;

    private String userId;

    private List<CartItemResponse> items;

    private Integer totalQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}