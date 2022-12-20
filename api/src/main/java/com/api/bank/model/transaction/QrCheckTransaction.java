package com.api.bank.model.transaction;

import lombok.Data;

@Data
public class QrCheckTransaction {

    private String operationId;
    private String token;
    private String label;
    private String debitAccount;
    private double amount;

    public QrCheckTransaction(String operationId, String token, double amount, String debitAccount) {
        this.operationId = operationId;
        this.token = token;
        this.amount = amount;
        this.debitAccount = debitAccount;
    }
}
