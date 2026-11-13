package com.ecommerce.product.service.impl;

import com.ecommerce.product.dto.request.ProductVariantRequest;
import com.ecommerce.product.dto.response.ProductVariantClientResponse;
import com.ecommerce.product.dto.response.ProductVariantResponse;
import com.ecommerce.product.entity.OutboxEvent;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.entity.ProductVariant;
import com.ecommerce.product.event.ProductVariantCreatedEvent;
import com.ecommerce.product.exception.BusinessException;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.mapper.ProductVariantMapper;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.repository.ProductVariantRepository;
import com.ecommerce.product.service.OutboxEventService;
import com.ecommerce.product.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ProductVariantMapper productVariantMapper;
    private final ObjectMapper objectMapper;
    private final OutboxEventService outboxEventService;

    @Override
    public List<ProductVariantResponse> create(List<ProductVariantRequest> productVariantRequests) {
        Set<String> productIds = productVariantRequests.stream()
                .map(ProductVariantRequest::getProductId)
                .collect(Collectors.toSet());

        Map<String, Product> products = productRepository.findAllById(productIds)
                .stream().collect(Collectors.toMap(Product::getId, Function.identity()));

        for (String productId : productIds) {
            if (!products.containsKey(productId)) {
                throw new BusinessException("PRODUCT_DOES_NOT_EXIST", "Product with id " + productId + " not found");
            }
        }

        List<ProductVariant> productVariants = productVariantRequests.stream()
                .map(productVariantRequest -> {
                    Product product = products.get(productVariantRequest.getProductId());
                    return  productVariantMapper.toEntity(productVariantRequest, product);
                })
                .toList();

        List<ProductVariant> savedProductVariant = productVariantRepository.saveAll(productVariants);

        List<OutboxEvent> outboxEvents = IntStream.range(0, savedProductVariant.size())
                .mapToObj(i -> buildOutboxEvent(
                        savedProductVariant.get(i),
                        productVariantRequests.get(i).getQuantity()
                ))
                .toList();

        outboxEventService.save(outboxEvents);

        return savedProductVariant.stream().map(productVariantMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public ProductVariantResponse update(String id, ProductVariantRequest productVariantRequest) {
        String productId = productVariantRequest.getProductId();

        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new BusinessException("PRODUCTVARIANT_DOES_NOT_EXIST", "ProductVariant with id " + id + " not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException("PRODUCT_DOES_NOT_EXIST", "Product with id " + productId + " not found"));

        ProductVariant productVariantUpdate = productVariantMapper.toEntity(productVariantRequest, product);
        productVariantUpdate.setId(productVariant.getId());

        ProductVariant savedProductVariant = productVariantRepository.save(productVariantUpdate);

        return productVariantMapper.toResponse(savedProductVariant);
    }

    @Override
    public List<ProductVariantResponse> findByProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException("PRODUCT_DOES_NOT_EXIST", "Product with id " + productId + " not found");
        }

        return productVariantRepository.findByProductId(productId)
                .stream()
                .map(productVariantMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(String id) {
        ProductVariant productVariant = productVariantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant with id " + id + " not found"));

        productVariantRepository.delete(productVariant);
    }

    @Override
    public void deleteByProduct(String productId) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException("PRODUCT_DOES_NOT_EXIST", "Product with id " + productId + " not found");
        }

        productVariantRepository.deleteAllByProductId(productId);
    }

    @Override
    public List<ProductVariantClientResponse> getByIds(List<String> variantIds) {
        if (variantIds == null || variantIds.isEmpty()) {
            return List.of();
        }

        return productVariantRepository.findAllByIdsWithProduct(variantIds)
                .stream()
                .map(productVariantMapper::toClientResponse)
                .toList();
    }

    @Override
    public ProductVariantClientResponse getById(String variantId) {
        ProductVariant productVariant = productVariantRepository.findByIdWithProduct(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductVariant with id " + variantId + " not found"));

        return productVariantMapper.toClientResponse(productVariant);
    }

    private OutboxEvent buildOutboxEvent(ProductVariant variant, Integer quantity) {
        String eventId = UUID.randomUUID().toString();

        ProductVariantCreatedEvent event = ProductVariantCreatedEvent.builder()
                .eventId(eventId)
                .variantId(variant.getId())
                .productId(variant.getProduct().getId())
                .quantity(quantity)
                .build();

        try {
            return OutboxEvent.builder()
                    .id(eventId)
                    .topic("product-variant-created")
                    .aggregateType("PRODUCT_VARIANT")
                    .aggregateId(variant.getId())
                    .eventType("PRODUCT_VARIANT_CREATED")
                    .payload(objectMapper.writeValueAsString(event))
                    .status(OutboxEvent.OutboxStatus.NEW)
                    .retryCount(0)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Cannot serialize ProductVariantCreatedEvent", e
            );
        }
    }
}
