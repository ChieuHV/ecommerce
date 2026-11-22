package com.ecommerce.inventory.service.impl;

import com.ecommerce.inventory.dto.request.InventoryRequest;
import com.ecommerce.inventory.dto.response.InventoryResponse;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.exception.BusinessException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
            throw new BusinessException(
                    "INVENTORY_REQUEST_VALIDATION_FAILED",
                    violations.toString()
            );
        }

        if (inventoryRepository.existsByVariantId(inventoryRequest.getVariantId())) {
            throw new BusinessException(
                    "INVENTORY_ALREADY_EXISTS",
                    "Inventory already exists for variant " + inventoryRequest.getVariantId()
            );
        }

        Inventory inventory = inventoryMapper.toEntity(inventoryRequest);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toInventoryResponse(savedInventory);
    }

    @Override
    @Transactional
    public List<InventoryResponse> update(List<InventoryRequest> inventoryRequests) {

        if (inventoryRequests == null || inventoryRequests.isEmpty()) {
            throw new BusinessException(
                    "INVENTORY_REQUESTS_EMPTY",
                    "At least one inventory item is required"
            );
        }

        for (InventoryRequest request : inventoryRequests) {

            if (request.getVariantId() == null || request.getVariantId().isBlank()) {
                throw new BusinessException(
                        "VARIANT_ID_REQUIRED",
                        "Variant ID is required"
                );
            }

            if (request.getQuantity() == null || request.getQuantity() < 0) {
                throw new BusinessException(
                        "INVALID_INVENTORY_QUANTITY",
                        "Quantity must be greater than or equal to zero"
                );
            }
        }

        Set<String> variantIds = inventoryRequests.stream()
                .map(InventoryRequest::getVariantId)
                .collect(Collectors.toSet());

        if (variantIds.size() != inventoryRequests.size()) {
            throw new BusinessException(
                    "DUPLICATE_VARIANT_IDS",
                    "Duplicate variant IDs are not allowed"
            );
        }

        List<String> sortedVariantIds = variantIds.stream().sorted().toList();

        List<Inventory> inventories = inventoryRepository.findAllByVariantIdInForUpdate(sortedVariantIds);

        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getVariantId,
                        Function.identity()
                ));

        List<InventoryResponse> responses = new ArrayList<>();

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            if (inventory == null) {
                throw new BusinessException(
                        "INVENTORY_NOT_FOUND",
                        "Inventory not found for variant: " + request.getVariantId()
                );
            }

            if (request.getQuantity() < inventory.getReservedQuantity()) {
                throw new BusinessException(
                        "QUANTITY_LESS_THAN_RESERVED",
                        "Quantity cannot be less than reserved quantity"
                );
            }

            inventory.setQuantity(request.getQuantity());
            inventory.setUpdatedDate(LocalDateTime.now());

            responses.add(inventoryMapper.toInventoryResponse(inventory));
        }

        return responses;
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
    public List<InventoryResponse> reserveInventories(List<InventoryRequest> inventoryRequests) {

        if (inventoryRequests == null || inventoryRequests.isEmpty()) {
            throw new BusinessException(
                    "INVENTORY_REQUESTS_EMPTY",
                    "At least one inventory item is required"
            );
        }

        Set<String> variantIds = inventoryRequests.stream()
                .map(InventoryRequest::getVariantId)
                .collect(Collectors.toSet());

        if (variantIds.size() != inventoryRequests.size()) {
            throw new BusinessException(
                    "DUPLICATE_VARIANT_IDS",
                    "Duplicate variant IDs are not allowed"
            );
        }

        List<String> sortedVariantIds = variantIds.stream().sorted().toList();

        List<Inventory> inventories = inventoryRepository.findAllByVariantIdInForUpdate(sortedVariantIds);

        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getVariantId,
                        Function.identity()
                ));

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            if (inventory == null) {
                throw new ResourceNotFoundException(
                        "Inventory with variant id "
                                + request.getVariantId() + " not found"
                );
            }

            int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();

            if (availableQuantity < request.getQuantity()) {
                throw new BusinessException(
                        "INSUFFICIENT_INVENTORY",
                        "Insufficient available inventory for variant: "
                                + request.getVariantId()
                );
            }
        }

        List<InventoryResponse> responses = new ArrayList<>();

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() + request.getQuantity()
            );

            inventory.setUpdatedDate(LocalDateTime.now());

            responses.add(inventoryMapper.toInventoryResponse(inventory));
        }

        return responses;
    }

    @Override
    @Transactional
    public List<InventoryResponse> releaseReservedInventories(List<InventoryRequest> inventoryRequests) {

        if (inventoryRequests == null || inventoryRequests.isEmpty()) {
            throw new BusinessException(
                    "INVENTORY_REQUESTS_EMPTY",
                    "At least one inventory item is required"
            );
        }

        Set<String> variantIds = inventoryRequests.stream()
                .map(InventoryRequest::getVariantId)
                .collect(Collectors.toSet());

        if (variantIds.size() != inventoryRequests.size()) {
            throw new BusinessException(
                    "DUPLICATE_VARIANT_IDS",
                    "Duplicate variant IDs are not allowed"
            );
        }

        List<String> sortedVariantIds = variantIds.stream()
                .sorted()
                .toList();

        List<Inventory> inventories =
                inventoryRepository.findAllByVariantIdInForUpdate(sortedVariantIds);

        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getVariantId,
                        Function.identity()
                ));

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            if (inventory == null) {
                throw new ResourceNotFoundException(
                        "Inventory with variant id "
                                + request.getVariantId() + " not found"
                );
            }

            if (request.getQuantity() <= 0) {
                throw new BusinessException(
                        "INVALID_INVENTORY_QUANTITY",
                        "Quantity must be greater than zero"
                );
            }

            if (inventory.getReservedQuantity() < request.getQuantity()) {
                throw new BusinessException(
                        "INSUFFICIENT_RESERVED_INVENTORY",
                        "Release quantity exceeds reserved inventory for variant: "
                                + request.getVariantId()
                );
            }
        }

        List<InventoryResponse> responses = new ArrayList<>();

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() - request.getQuantity()
            );

            inventory.setUpdatedDate(LocalDateTime.now());

            responses.add(inventoryMapper.toInventoryResponse(inventory));
        }

        return responses;
    }

    @Override
    @Transactional
    public List<InventoryResponse> confirmReservedInventories(List<InventoryRequest> inventoryRequests) {

        if (inventoryRequests == null || inventoryRequests.isEmpty()) {
            throw new BusinessException(
                    "INVENTORY_REQUESTS_EMPTY",
                    "At least one inventory item is required"
            );
        }

        Set<String> variantIds = inventoryRequests.stream()
                .map(InventoryRequest::getVariantId)
                .collect(Collectors.toSet());

        if (variantIds.size() != inventoryRequests.size()) {
            throw new BusinessException(
                    "DUPLICATE_VARIANT_IDS",
                    "Duplicate variant IDs are not allowed"
            );
        }

        List<String> sortedVariantIds = variantIds.stream().sorted().toList();

        List<Inventory> inventories = inventoryRepository.findAllByVariantIdInForUpdate(sortedVariantIds);

        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getVariantId,
                        Function.identity()
                ));

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            if (inventory == null) {
                throw new ResourceNotFoundException(
                        "Inventory with variant id "
                                + request.getVariantId() + " not found"
                );
            }

            int quantity = request.getQuantity();

            if (inventory.getReservedQuantity() < quantity) {
                throw new BusinessException(
                        "INSUFFICIENT_RESERVED_INVENTORY",
                        "Not enough reserved inventory for variant: "
                                + request.getVariantId()
                );
            }
        }

        List<InventoryResponse> responses = new ArrayList<>();

        for (InventoryRequest request : inventoryRequests) {

            Inventory inventory = inventoryMap.get(request.getVariantId());

            int quantity = request.getQuantity();

            inventory.setQuantity(inventory.getQuantity() - quantity);

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() - quantity
            );

            inventory.setUpdatedDate(LocalDateTime.now());

            responses.add(inventoryMapper.toInventoryResponse(inventory));
        }

        return responses;
    }
}
