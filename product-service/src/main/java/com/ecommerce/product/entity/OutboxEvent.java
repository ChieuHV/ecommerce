package com.ecommerce.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public enum OutboxStatus {
        NEW,
        PROCESSING,
        SENT,
        FAILED,
        DEAD_LETTER
    }
}