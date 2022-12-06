package com.api.bank.model;

import lombok.Data;

@Data
public class ObjectResponse {
    private String message;
    private Object data;
    private boolean isValid;

    public ObjectResponse(String message, Object data) {
        this.message = message;
        this.data = data;
    }
    public ObjectResponse(String message, Object data, boolean isValid) {
        this.message = message;
        this.data = data;
        this.isValid = isValid;
    }

    public ObjectResponse(String message) {
        this.message = message;
    }
}

