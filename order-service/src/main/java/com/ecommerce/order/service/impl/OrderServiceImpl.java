package com.ecommerce.order.service.impl;

import com.ecommerce.order.client.ProductVariantClient;
import com.ecommerce.order.common.response.ApiResponse;
import com.ecommerce.order.dto.request.OrderItemRequest;
import com.ecommerce.order.dto.request.OrderRequest;
import com.ecommerce.order.dto.response.OrderItemResponse;
import com.ecommerce.order.dto.response.OrderResponse;
import com.ecommerce.order.dto.response.ProductVariantClientResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.enums.OrderStatus;
import com.ecommerce.order.exception.BusinessException;
import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantClient productVariantClient;
    private final OrderMapper orderMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponse createOrder(OrderRequest orderRequest) {
        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new BusinessException("ORDER_ITEM_REQUIRED", "Order request items cannot be empty");
        }

        Set<String> uniqueVariantIds = new HashSet<>();

        for (OrderItemRequest orderItemRequest : orderRequest.getItems()) {
            String variantId = orderItemRequest.getVariantId().trim();

            if (!uniqueVariantIds.add(variantId)) {
                throw new BusinessException("DUPLICATE", "Duplicate variant ID: " + variantId);
            }
        }

        List<String> variantIds = orderRequest.getItems().stream()
                .map(request -> request.getVariantId().trim())
                .toList();

        ApiResponse<List<ProductVariantClientResponse>> apiResponse = productVariantClient.getVariantsByIds(variantIds);

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new BusinessException("VARIANT_CLIENT_NOT_FOUND", "Product variant not found");
        }

        List<ProductVariantClientResponse> variants = apiResponse.getData();

        if (variants.size() != uniqueVariantIds.size()) {
            throw new BusinessException("VARIANT_NOT_FOUND", "One or more product variants do not exist");
        }

        Map<String, ProductVariantClientResponse> variantMap =
                variants.stream()
                        .collect(Collectors.toMap(
                                ProductVariantClientResponse::getVariantId,
                                Function.identity()
                        ));

        List<OrderItem> orderItems = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {

            ProductVariantClientResponse variant = variantMap.get(itemRequest.getVariantId());

            if (variant == null) {
                throw new BusinessException("VARIANT_NOT_FOUND", "Variant not found: " + itemRequest.getVariantId());
            }

            BigDecimal itemSubtotal = variant.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            subtotal = subtotal.add(itemSubtotal);

            OrderItem orderItem = OrderItem.builder()
                    .productId(variant.getProductId())
                    .variantId(variant.getVariantId())
                    .productName(variant.getName())
                    .variantName(buildVariantName(variant.getAttributes()))
                    .sku(variant.getSku())
                    .imageUrl(variant.getImageUrl())
                    .unitPrice(variant.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .subtotal(itemSubtotal)
                    .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                    .build();

            orderItems.add(orderItem);
        }

        Order savedOrder = orderRepository.save(this.buildOrder(orderRequest, subtotal));

        orderItems.forEach(item -> item.setOrder(savedOrder));

        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

        return orderMapper.toResponse(savedOrder, savedItems);
    }

    private Order buildOrder(OrderRequest orderRequest, BigDecimal subtotal) {

        BigDecimal shippingFee = BigDecimal.ZERO;
        BigDecimal discountAmount = BigDecimal.ZERO;

        return Order.builder()
                .orderNumber(generateOrderNumber())
                .userId("test-id")
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .discountAmount(discountAmount)
                .totalAmount(subtotal.add(shippingFee).subtract(discountAmount))
                .currency("VND")
                .recipientName(orderRequest.getRecipientName())
                .phoneNumber(orderRequest.getPhoneNumber())
                .addressLine(orderRequest.getAddressLine())
                .ward(orderRequest.getWard())
                .district(orderRequest.getDistrict())
                .city(orderRequest.getCity())
                .country(orderRequest.getCountry())
                .postalCode(orderRequest.getPostalCode())
                .customerNote(orderRequest.getCustomerNote())
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now(ZoneId.systemDefault()))
                .build();
    }

    private String buildVariantName(Map<String, String> attributes) {

        if (attributes == null || attributes.isEmpty()) {
            return null;
        }

        return attributes.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.joining(" / "));
    }

    private String generateOrderNumber() {
        return "ORD-" +
                LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) +
                "-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }
}
