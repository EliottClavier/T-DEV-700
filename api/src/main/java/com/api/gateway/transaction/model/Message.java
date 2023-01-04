package com.api.gateway.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Message class used to send the simplest message to TPE or SHOP
 */
@Data
@AllArgsConstructor
public class Message {
    private String message;
    private Object type;
}

