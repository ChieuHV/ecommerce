package com.ecommerce.product.repository;

import com.ecommerce.product.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List<ProductVariant> findByProductId(String productId);

    void deleteAllByProductId(String productId);

    @Query("""
            SELECT pv
            FROM ProductVariant pv
            JOIN FETCH pv.product p
            WHERE pv.id IN :variantIds
            """)
    List<ProductVariant> findAllByIdsWithProduct(List<String> variantIds);
}
