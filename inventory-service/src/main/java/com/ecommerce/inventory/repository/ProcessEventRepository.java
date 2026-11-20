package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessEventRepository extends JpaRepository<ProcessedEvent, String> {
}
