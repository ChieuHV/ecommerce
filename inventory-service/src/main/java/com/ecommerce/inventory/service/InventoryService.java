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

    List<InventoryResponse> reserveInventories(List<InventoryRequest> inventoryRequests);

    List<InventoryResponse> releaseReservedInventories(List<InventoryRequest> inventoryRequests);

    List<InventoryResponse> confirmReservedInventories(List<InventoryRequest> inventoryRequest);

}