package com.ecommerce.cart.service.impl;

import com.ecommerce.cart.client.ProductVariantClient;
import com.ecommerce.cart.common.response.ApiResponse;
import com.ecommerce.cart.dto.request.CartItemRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;
import com.ecommerce.cart.dto.response.CartResponse;
import com.ecommerce.cart.dto.response.ProductVariantClientResponse;
import com.ecommerce.cart.entity.Cart;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.exception.BusinessException;
import com.ecommerce.cart.exception.ResourceNotFoundException;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import com.ecommerce.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantClient productVariantClient;

    @Override
    @Transactional
    public CartResponse save(List<CartItemRequest> cartItemRequests) {

        if (cartItemRequests == null || cartItemRequests.isEmpty()) {
            throw new BusinessException("EMPTY_CART", "Cart items must not be empty");
        }

        Set<String> uniqueVariantIds = new HashSet<>();

        for (CartItemRequest request : cartItemRequests) {
            String variantId = request.getVariantId().trim();

            if (!uniqueVariantIds.add(variantId)) {
                throw new BusinessException(
                        "DUPLICATE",
                        "Duplicate variant ID: " + variantId
                );
            }
        }

        List<String> variantIds = cartItemRequests.stream()
                .map(request -> request.getVariantId().trim())
                .toList();

        ApiResponse<List<ProductVariantClientResponse>> apiResponse = productVariantClient.getVariantsByIds(variantIds);

        if (apiResponse == null) {
            throw new BusinessException("VARIANT_CLIENT_NOT_FOUND", "Product variant not found");
        }

        List<ProductVariantClientResponse> variants = productVariantClient.getVariantsByIds(variantIds).getData();

        if (variants.size() != uniqueVariantIds.size()) {
            throw new BusinessException(
                    "VARIANT_NOT_FOUND",
                    "One or more product variants do not exist"
            );
        }

        LocalDateTime currentDateTime = LocalDateTime.now(ZoneId.systemDefault());

        List<CartItem> existingItems = cartItemRepository.findByCartUserIdAndVariantIdIn("test-id", variantIds);

        Map<String, CartItem> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getVariantId,
                        Function.identity()
                ));

        Cart cart = cartRepository.findByUserId("test-id")
                .orElseGet(() ->
                        cartRepository.save(
                                Cart.builder()
                                        .userId("test-id")
                                        .createdAt(currentDateTime)
                                        .build()
                        )
                );

        List<CartItem> cartItems = new ArrayList<>();

        for (CartItemRequest request : cartItemRequests) {

            String variantId = request.getVariantId().trim();

            CartItem existingItem = existingItemMap.get(variantId);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
                existingItem.setUpdatedAt(currentDateTime);
                cartItems.add(existingItem);
            } else {
                CartItem cartItem = CartItem.builder()
                        .variantId(variantId)
                        .quantity(request.getQuantity())
                        .createdAt(currentDateTime)
                        .cart(cart)
                        .build();

                cartItems.add(cartItem);
            }
        }

        cartItemRepository.saveAll(cartItems);

        return buildCartResponse(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCartUserId(cart.getUserId());

        if (cartItems.isEmpty()) {
            return CartResponse.builder()
                    .id(cart.getId())
                    .userId(cart.getUserId())
                    .items(List.of())
                    .totalQuantity(0)
                    .createdAt(cart.getCreatedAt())
                    .updatedAt(cart.getUpdatedAt())
                    .build();
        }

        List<String> variantIds = cartItems.stream()
                .map(CartItem::getVariantId)
                .distinct()
                .toList();

        ApiResponse<List<ProductVariantClientResponse>> apiResponse = productVariantClient.getVariantsByIds(variantIds);

        if (apiResponse == null) {
            throw new BusinessException("VARIANT_CLIENT_NOT_FOUND", "Product variant not found");
        }

        List<ProductVariantClientResponse> variants = productVariantClient.getVariantsByIds(variantIds).getData();

        Map<String, ProductVariantClientResponse> variantMap =
                variants.stream()
                        .collect(Collectors.toMap(
                                ProductVariantClientResponse::getVariantId,
                                Function.identity()
                        ));

        List<CartItemResponse> cartItemResponses =
                cartItems.stream()
                        .map(item -> {

                            ProductVariantClientResponse variant = variantMap.get(item.getVariantId());

                            if (variant == null) {
                                throw new BusinessException(
                                        "VARIANT_NOT_FOUND",
                                        "Product variant not found: " + item.getVariantId()
                                );
                            }

                            BigDecimal subTotal = variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

                            return CartItemResponse.builder()
                                    .id(item.getId())
                                    .variantId(item.getVariantId())
                                    .productId(variant.getProductId())
                                    .productName(variant.getName())
                                    .imageUrl(variant.getImageUrl())
                                    .sku(variant.getSku())
                                    .barcode(variant.getBarcode())
                                    .attributes(variant.getAttributes())
                                    .price(variant.getPrice())
                                    .quantity(item.getQuantity())
                                    .subtotal(subTotal)
                                    .createdAt(item.getCreatedAt())
                                    .updatedAt(item.getUpdatedAt())
                                    .build();
                        })
                        .toList();

        Integer totalQuantity = cartItemResponses.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        BigDecimal totalPrice = cartItemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cartItemResponses)
                .totalQuantity(totalQuantity)
                .totalPrice(totalPrice)
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    @Override
    public CartResponse findByUserId() {
        String userId = "test-id";

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart with user id " + userId + " not found"
                        )
                );

        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public void delete(String id) {
        Cart cart = cartRepository.findByUserIdAndId("test-id", id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart with id " + id + " not found"
                        )
                );

        cartItemRepository.deleteByCartId(cart.getId());

        cartRepository.delete(cart);
    }
}