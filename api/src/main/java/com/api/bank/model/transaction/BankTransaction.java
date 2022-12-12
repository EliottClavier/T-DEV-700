package com.api.bank.model.transaction;

import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class BankTransaction {

    private String OperationId;
    private UUID shopId;
    private String label;
    private String cardId;
    private String checkToken;
    private float amount;
    private Date date;
    private PaymentMethod paymentMethod;

    public BankTransaction(String operationId, UUID shopId, String label, String cardId, String checkToken, float amount, Date date, PaymentMethod paymentMethod) {
        OperationId = operationId;
        this.shopId = shopId;
        this.label = label;
        this.cardId = cardId;
        this.checkToken = checkToken;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;

        if(date == null) {
            date = new Date();
        }
    }

    public BankTransaction() {
        if(date == null) {
            date = new Date();
        }
    }
}

