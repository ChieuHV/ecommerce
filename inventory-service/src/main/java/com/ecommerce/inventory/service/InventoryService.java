package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse save(InventoryRequest inventoryRequest);

    InventoryResponse getById(String id);

    InventoryResponse getByVariantId(String variantId);

    List<InventoryResponse> getAll();

    InventoryResponse increaseInventory(String variantId, int quantity);

    InventoryResponse decreaseInventory(String variantId, int quantity);
}