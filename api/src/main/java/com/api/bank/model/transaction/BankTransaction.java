package com.api.bank.model.transaction;

import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class BankTransaction {

    private String OperationId;
    private UUID shopId;
    private String depositUsername;
    private String label;
    private String meansOfPaymentId;

    private float amount;
    private Date date;
    private PaymentMethod paymentMethod;

    public BankTransaction(String operationId, UUID shopId, String label, String meansOfPaymentId, float amount, Date date, PaymentMethod paymentMethod) {
        OperationId = operationId;
        this.shopId = shopId;
        this.label = label;
        this.meansOfPaymentId = meansOfPaymentId;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;

        init();
    }
    public BankTransaction(String operationId, String depositUsername, String label, String meansOfPaymentId, float amount, Date date, PaymentMethod paymentMethod) {
        OperationId = operationId;
        this.depositUsername = depositUsername;
        this.label = label;
        this.meansOfPaymentId = meansOfPaymentId;
        this.amount = amount;
        this.date = date;
        this.paymentMethod = paymentMethod;

        init();
    }


    public BankTransaction() {
        init();
    }

    private void init() {
        if (date == null) {
            date = new Date();
        }
    }
}


