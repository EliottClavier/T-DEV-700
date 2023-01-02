package com.api.gateway.transaction.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageClient {
    @JsonProperty("message")
    private Object message;

    @JsonProperty("type")
    private Object type;
}
