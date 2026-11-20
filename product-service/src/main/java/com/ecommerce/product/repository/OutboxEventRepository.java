package com.ecommerce.product.repository;

import com.ecommerce.product.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    @Query(value = """
    SELECT *
    FROM outbox_event
    WHERE status = 'NEW'
    ORDER BY created_at ASC
    LIMIT :limit
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<OutboxEvent> findPendingEventsForUpdate(@Param("limit") int limit);
}
