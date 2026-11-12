package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.service.InventoryService;
import com.ecommerce.inventory.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getAll(){
        List<InventoryResponse> inventories = inventoryService.getAll();

        return ResponseEntity.ok(ApiResponse.success("Inventories retrieved successfully", inventories));
    }
}
