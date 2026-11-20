package com.ecommerce.inventory.mapper;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InventoryMapper {

    public Inventory toEntity(InventoryRequest inventoryRequest) {
        return Inventory.builder()
                .variantId(inventoryRequest.getVariantId())
                .quantity(inventoryRequest.getQuantity())
                .reservedQuantity(0)
                .createdDate(LocalDateTime.now())
                .build();
    }

    public InventoryResponse toInventoryResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .variantId(inventory.getVariantId())
                .quantity(inventory.getQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .createdDate(inventory.getCreatedDate())
                .updatedDate(inventory.getUpdatedDate())
                .build();
    }
}