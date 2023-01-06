package com.api.gateway.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * MessageCancelTpe class used to send message to TPE when Shop cancels transaction
 * This class allows to hold typeTpe which corresponds to the WebSocketStatus of TPE
 * after automatically synchronize following transaction cancellation
 */
@Data
@AllArgsConstructor
public class MessageCancelTpe {
    private String message;
    private Object type;
    private Object typeTpe;
}

