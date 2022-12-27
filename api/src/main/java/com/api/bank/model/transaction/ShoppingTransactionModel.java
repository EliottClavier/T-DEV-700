package com.api.bank.model.transaction;

import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;

@Data
public class ShoppingTransactionModel {

    private String operationId;
    private String depositUsername;
    private String label;
    private String meansOfPaymentId;
    private float amount;
    private Date date;
    private PaymentMethod paymentMethod;

    public ShoppingTransactionModel(String operationId, String depositUsername, String label, String meansOfPaymentId, float amount, PaymentMethod paymentMethod) {
        this.operationId = operationId;
        this.depositUsername = depositUsername;
        this.label = label;
        this.meansOfPaymentId = meansOfPaymentId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        init();
    }


    public ShoppingTransactionModel() {
        init();
    }

    private void init() {
        if (date == null) {
            date = new Date();
        }
    }
}


