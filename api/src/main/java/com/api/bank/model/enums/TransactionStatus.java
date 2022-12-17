package com.api.bank.model.enums;

public enum TransactionStatus {
    SUCCESS,
    FAILED,
    PAYMENT_ERROR,
    MEANS_OF_PAYMENT_ERROR,
    CARD_ERROR,
    CHECK_ERROR,
    BANK_ERROR,
    INSUFFICIENT_FUNDS_ERROR,
    OPERATION_PENDING_ERROR,
}
