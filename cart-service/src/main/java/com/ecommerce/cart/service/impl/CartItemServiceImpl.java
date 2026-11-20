package com.ecommerce.cart.service.impl;

import com.ecommerce.cart.client.ProductVariantClient;
import com.ecommerce.cart.common.response.ApiResponse;
import com.ecommerce.cart.dto.request.CartItemRequest;
import com.ecommerce.cart.dto.response.CartItemResponse;
import com.ecommerce.cart.dto.response.ProductVariantClientResponse;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.exception.BusinessException;
import com.ecommerce.cart.exception.ResourceNotFoundException;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductVariantClient productVariantClient;

    @Override
    @Transactional
    public CartItemResponse update(String id, CartItemRequest cartItemRequest) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item with id " + id + " not found"
                        )
                );

        CartItem updatedCart;

        if (cartItem.getVariantId().equals(cartItemRequest.getVariantId())) {
            cartItem.setQuantity(cartItemRequest.getQuantity());
            cartItem.setUpdatedAt(LocalDateTime.now(ZoneId.systemDefault()));

            updatedCart = cartItemRepository.save(cartItem);
        } else {
            Optional<CartItem> productVariantItem = cartItemRepository.findByCartUserIdAndVariantId("test-id", cartItemRequest.getVariantId());

            if (productVariantItem.isPresent()) {
                productVariantItem.get().setQuantity(productVariantItem.get().getQuantity() + cartItemRequest.getQuantity());

                updatedCart = cartItemRepository.save(productVariantItem.get());

                cartItemRepository.delete(cartItem);
            } else {
                cartItem.setVariantId(cartItemRequest.getVariantId());
                cartItem.setQuantity(cartItemRequest.getQuantity());
                cartItem.setUpdatedAt(LocalDateTime.now(ZoneId.systemDefault()));

                updatedCart = cartItemRepository.save(cartItem);
            }
        }

        ApiResponse<ProductVariantClientResponse> apiResponse = productVariantClient.getVariantById(updatedCart.getVariantId());

        if (apiResponse == null) {
            throw new BusinessException("VARIANT_CLIENT_NOT_FOUND", "Product variant not found");
        }

        ProductVariantClientResponse productVariantClientResponse = apiResponse.getData();

        if (productVariantClientResponse == null) {
            throw new BusinessException("VARIANT_NOT_FOUND", "Product variant not found");
        }

        BigDecimal subTotal = productVariantClientResponse.getPrice().multiply(BigDecimal.valueOf(updatedCart.getQuantity()));

        return CartItemResponse.builder()
                .id(updatedCart.getId())
                .variantId(updatedCart.getVariantId())
                .productId(productVariantClientResponse.getProductId())
                .productName(productVariantClientResponse.getName())
                .imageUrl(productVariantClientResponse.getImageUrl())
                .sku(productVariantClientResponse.getSku())
                .barcode(productVariantClientResponse.getBarcode())
                .attributes(productVariantClientResponse.getAttributes())
                .price(productVariantClientResponse.getPrice())
                .quantity(updatedCart.getQuantity())
                .subtotal(subTotal)
                .createdAt(updatedCart.getCreatedAt())
                .updatedAt(updatedCart.getUpdatedAt())
                .build();
    }

    @Override
    public void delete(String id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item with id " + id + " not found"
                        )
                );

        cartItemRepository.delete(cartItem);
    }
}
