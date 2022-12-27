package com.api.bank.model.exception;

import com.api.bank.model.enums.TransactionStatus;

import java.util.concurrent.ExecutionException;


public class BankTransactionException extends Exception {
    private String message;

    private TransactionStatus transactionStatus;

    public BankTransactionException(String message, String operationId) {
        super(message);
        this.message = message;
    }

    public BankTransactionException( TransactionStatus transactionStatus, String operationId, String message ) {
        super(message);
        this.message = message;
        this.transactionStatus = transactionStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
