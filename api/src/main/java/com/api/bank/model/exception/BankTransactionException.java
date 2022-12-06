package com.api.bank.model.exception;

import com.api.bank.model.transaction.TransactionStatus;


public class BankTransactionException extends Exception {
    private String message;

    private String OperationId;

    private TransactionStatus transactionStatus;

    public BankTransactionException(String message, String operationId) {
        super(message);
        this.message = message;
        OperationId = operationId;
    }

    public BankTransactionException( TransactionStatus transactionStatus, String operationId, String message ) {
        super(message);
        this.message = message;
        OperationId = operationId;
        this.transactionStatus = transactionStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperationId() {
        return OperationId;
    }

    public void setOperationId(String operationId) {
        OperationId = operationId;
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
