package com.ecommerce.product.repository;

import com.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :productId")
    Optional<Product> findByIdWithCategory(@Param("productId") String productId);

    @Query("SELECT p FROM Product p JOIN FETCH p.category")
    List<Product> findAllWithCategory();

    List<Product> findByNameInIgnoreCase(List<String> names);
}
