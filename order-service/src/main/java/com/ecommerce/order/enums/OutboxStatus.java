package com.ecommerce.order.enums;

public enum OutboxStatus {

    NEW,

    PROCESSING,

    SENT,

    FAILED,

    DEAD_LETTER
}