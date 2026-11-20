package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.common.response.ApiResponse;
import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAll() {
        List<InventoryResponse> inventories = inventoryService.getAll();

        return ResponseEntity.ok(ApiResponse.success("Inventories retrieved successfully", inventories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getById(@PathVariable String id) {

        InventoryResponse inventory = inventoryService.getById(id);

        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @GetMapping("/variant/{variantId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getByVariantId(@PathVariable String variantId) {

        InventoryResponse inventory = inventoryService.getByVariantId(variantId);

        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved successfully", inventory));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<InventoryResponse>> save(@Valid @RequestBody InventoryRequest inventoryRequest) {

        InventoryResponse inventory = inventoryService.save(inventoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Inventory created successfully", inventory));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> update(@RequestBody List<@Valid InventoryRequest> inventoryRequests) {

        List<InventoryResponse> inventories = inventoryService.update(inventoryRequests);

        return ResponseEntity.ok(ApiResponse.success("Inventories updated successfully", inventories));
    }

    @PatchMapping("/increase")
    public ResponseEntity<ApiResponse<InventoryResponse>> increaseInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {

        InventoryResponse inventory = inventoryService.increaseInventory(inventoryRequest);

        return ResponseEntity.ok(ApiResponse.success("Inventory increased successfully", inventory));
    }

    @PatchMapping("/decrease")
    public ResponseEntity<ApiResponse<InventoryResponse>> decreaseInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {

        InventoryResponse inventory = inventoryService.decreaseInventory(inventoryRequest);

        return ResponseEntity.ok(ApiResponse.success("Inventory decreased successfully", inventory));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> reserveInventories(@RequestBody List<@Valid InventoryRequest> inventoryRequests) {

        List<InventoryResponse> inventories = inventoryService.reserveInventories(inventoryRequests);

        return ResponseEntity.ok(ApiResponse.success("Inventories reserved successfully", inventories));
    }

    @PostMapping("/reserve")
    public ResponseEntity<ApiResponse<InventoryResponse>> reserveInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {

        InventoryResponse inventory = inventoryService.reserveInventory(inventoryRequest);

        return ResponseEntity.ok(ApiResponse.success("Inventory reserved successfully", inventory));
    }

    @PostMapping("/release")
    public ResponseEntity<ApiResponse<InventoryResponse>> releaseReservedInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {

        InventoryResponse inventory = inventoryService.releaseReservedInventory(inventoryRequest);

        return ResponseEntity.ok(ApiResponse.success("Reserved inventory released successfully", inventory));
    }
}
