package com.api.bank.model.enums;

/**
 * When an operation is written, it can be in different states.
 * During the transaction process, the status of the operation is "PENDING".
 * At the end of the transaction, it will be in the state of "CLOSED" or "CANCELED".
 */
public enum OperationStatus {
    PENDING,
    CLOSED,
    CANCELED,

}
