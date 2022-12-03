package com.api.bank.model;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

