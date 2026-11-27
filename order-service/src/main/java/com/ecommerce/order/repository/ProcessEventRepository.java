package com.ecommerce.order.repository;

import com.ecommerce.order.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessEventRepository extends JpaRepository<ProcessedEvent, String> {
}
