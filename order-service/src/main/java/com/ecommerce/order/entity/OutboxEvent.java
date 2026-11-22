package com.ecommerce.order.entity;

import com.ecommerce.order.enums.OutboxStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "outbox_event",
        indexes = {
                @Index(
                        name = "idx_outbox_status_created_at",
                        columnList = "status, created_at"
                ),
                @Index(
                        name = "idx_outbox_aggregate",
                        columnList = "aggregate_type, aggregate_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    @Id
    private String id;
    @Column(nullable = false, length = 150)
    private String topic;
    @Column(nullable = false, length = 100)
    private String aggregateType;
    @Column(nullable = false, length = 100)
    private String aggregateId;
    @Column(nullable = false, length = 150)
    private String eventType;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;
    @Column(nullable = false)
    private Integer retryCount;
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}