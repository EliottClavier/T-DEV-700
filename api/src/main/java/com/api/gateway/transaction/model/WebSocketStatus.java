package com.api.gateway.transaction.model;

/**
 * All messages possibly sent by Websocket server to client
 */
public enum WebSocketStatus {
    TRANSACTION_OPENED,
    TRANSACTION_CANCELLED,
    TRANSACTION_ERROR,
    TRANSACTION_TIMED_OUT,
    SYNCHRONIZED,
    SYNCHRONIZATION_ERROR,
    ALREADY_SYNCHRONIZED,
    TPE_INVALID,
    NOT_INVOLVED,
    NOT_FOUND,
    NO_TPE_FOUND,
    NO_TPE_SELECTED,
    INVALID_PAYMENT_METHOD,
    STILL_AVAILABLE,
    LOST_CONNECTION,
    ALREADY_IN_TRANSACTION,
    SERVER_RESTARTED
}
