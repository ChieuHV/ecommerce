package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {

    Optional<Inventory> findByVariantId(String variantId);

    boolean existsByVariantId(String variantId);

    List<Inventory> findAllByVariantIdIn(List<String> variantIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.variantId = :variantId
            """)
    Optional<Inventory> findByVariantIdForUpdate(String variantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT i
            FROM Inventory i
            WHERE i.variantId IN :variantIds
            ORDER BY i.variantId
            """)
    List<Inventory> findAllByVariantIdInForUpdate(List<String> variantIds);
}