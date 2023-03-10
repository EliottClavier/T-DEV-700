package com.api.bank.model.transaction;

import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class QrCheckTransactionModel {

    private String operationId;
    private String token;
    private String label;
    private UUID clientId;
    private double amount;
    private PaymentMethod paymentMethod;
    private Date date;

    public QrCheckTransactionModel(String operationId, String token, double amount, UUID clientId, PaymentMethod paymentMethod) {
        this.operationId = operationId;
        this.token = token;
        this.amount = amount;
        this.clientId = clientId;
        this.paymentMethod = paymentMethod;
        this.label = "CHECK";
        init();
    }
    public void init() {
        if (date == null) {
            date = new Date();
        }
        if (operationId == null) {
            operationId = UUID.randomUUID().toString();
        }
        if(label == null){
            label = "QrCheck buying " + operationId;
        }
    }
}
