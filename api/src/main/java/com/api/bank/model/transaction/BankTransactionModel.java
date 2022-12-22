package com.api.bank.model.transaction;

import com.api.bank.model.entity.Account;
import com.api.bank.model.entity.QrCheck;
import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class BankTransactionModel {
    private String operationId;
    private String label;
    private Account withdrawalAccount;
    private Account depositAccount;
    private QrCheck qrCheck;
    private double amount;
    private Date date;
    private PaymentMethod paymentMethod;


    public BankTransactionModel(ShoppingTransactionModel shoppingTransaction) {

        this.operationId = shoppingTransaction.getOperationId();
        this.label = shoppingTransaction.getLabel();
        this.amount = shoppingTransaction.getAmount();
        this.paymentMethod = shoppingTransaction.getPaymentMethod();
        this.date = shoppingTransaction.getDate();

        init();
    }

    public BankTransactionModel(QrCheckTransactionModel qrcheckTransaction) {

            this.operationId = qrcheckTransaction.getOperationId();
            this.label = qrcheckTransaction.getLabel();
            this.amount = qrcheckTransaction.getAmount();
            this.paymentMethod = qrcheckTransaction.getPaymentMethod();
            this.date = qrcheckTransaction.getDate();

            init();

    }

    private void init() {
        if (date == null) {
            date = new Date();
        }
        if (operationId == null) {
            operationId = UUID.randomUUID().toString();
        }
    }
}
