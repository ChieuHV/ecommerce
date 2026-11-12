package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.exception.ResourceNotFoundException;
import com.ecommerce.inventory.mapper.InventoryMapper;
import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final Validator validator;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public InventoryResponse save(InventoryRequest inventoryRequest) {
        Set<ConstraintViolation<InventoryRequest>> violations = validator.validate(inventoryRequest);

        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(violations.toString());
        }

        if (inventoryRepository.existsByVariantId(inventoryRequest.getVariantId())) {
            throw new IllegalArgumentException("Inventory already exists for variant " + inventoryRequest.getVariantId());
        }

        Inventory inventory = inventoryMapper.toEntity(inventoryRequest);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toInventoryResponse(savedInventory);
    }

    @Override
    public InventoryResponse getById(String id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory with id " + id + " not found"
                ));

        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    public InventoryResponse getByVariantId(String variantId) {
        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory with variant id " + variantId + " not found"
                ));

        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    public List<InventoryResponse> getAll() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toInventoryResponse)
                .toList();
    }

    @Override
    @Transactional
    public InventoryResponse increaseInventory(String variantId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory with variant id " + variantId + " not found"
                ));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setUpdatedDate(LocalDateTime.now());

        return inventoryMapper.toInventoryResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse decreaseInventory(String variantId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Inventory inventory = inventoryRepository.findByVariantId(variantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Inventory with variant id " + variantId + " not found"
                ));

        int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();

        if (availableQuantity < quantity) {
            throw new IllegalArgumentException("Insufficient available inventory");
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setUpdatedDate(LocalDateTime.now());

        return inventoryMapper.toInventoryResponse(inventoryRepository.save(inventory));
    }
}