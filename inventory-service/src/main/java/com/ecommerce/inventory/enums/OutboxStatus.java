package com.ecommerce.inventory.enums;

public enum OutboxStatus {

    NEW,

    PROCESSING,

    SENT,

    FAILED,

    DEAD_LETTER
}