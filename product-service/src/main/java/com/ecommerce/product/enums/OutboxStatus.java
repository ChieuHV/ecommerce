package com.ecommerce.product.enums;

public enum OutboxStatus {
    NEW,

    PROCESSING,

    SENT,

    FAILED,

    DEAD_LETTER
}
