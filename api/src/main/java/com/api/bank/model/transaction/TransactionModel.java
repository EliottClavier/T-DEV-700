package com.api.bank.model.transaction;

import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;

@Data
public class TransactionModel {

    private String OperationId;
    private String label;
    private String cardId;
    private String checkToken;
    private int amount;
    private String tokenShop;
    private String tokenTpe;
    private Date date;
    private PaymentMethod paymentMethod;
}

