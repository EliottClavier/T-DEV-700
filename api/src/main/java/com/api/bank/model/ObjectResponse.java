package com.api.bank.model;

import lombok.Data;

@Data
public class ObjectResponse {
    private String message;
    private Object data;
    private boolean status;

    public ObjectResponse(String message, Object data, boolean status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }
    public ObjectResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
    }


}

