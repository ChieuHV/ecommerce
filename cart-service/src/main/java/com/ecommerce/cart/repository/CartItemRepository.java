package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCartUserIdAndVariantIdIn(String userId, List<String> variantIds);

    List<CartItem> findByCartUserId(String userId);

    void deleteByCartId(String cartId);
}