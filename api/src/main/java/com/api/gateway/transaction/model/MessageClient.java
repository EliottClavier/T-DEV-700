package com.api.gateway.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MessageClient class represents the data possibly received by client (TPE or Shop)
 * It is only used in WebSocket tests to manipulate data received by client in subscription
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageClient {
    @JsonProperty("message")
    private Object message;

    @JsonProperty("type")
    private Object type;

    @JsonProperty("amount")
    private Object amount;
}
