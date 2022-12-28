package com.api.gateway.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageCancelTpe {
    private String message;
    private Object type;
    private Object typeTpe;
}

