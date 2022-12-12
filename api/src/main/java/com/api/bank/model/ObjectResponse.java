package com.api.bank.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ObjectResponse {
    private String message;
    private Object data;
    private HttpStatus status;

    public ObjectResponse(String message, Object data, HttpStatus status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public ObjectResponse(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}

