package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;

import java.util.List;

public interface InventoryService {

    InventoryResponse save(InventoryRequest inventoryRequest);

    List<InventoryResponse> update(List<InventoryRequest> inventoryRequests);

    InventoryResponse getById(String id);

    InventoryResponse getByVariantId(String variantId);

    List<InventoryResponse> getAll();

    InventoryResponse increaseInventory(InventoryRequest inventoryRequest);

    InventoryResponse decreaseInventory(InventoryRequest inventoryRequest);

    List<InventoryResponse> reserveInventories(List<InventoryRequest> inventoryRequests);

    InventoryResponse reserveInventory(InventoryRequest inventoryRequest);

    InventoryResponse releaseReservedInventory(InventoryRequest inventoryRequest);

}