package com.api.bank.model.transaction;

import com.api.bank.model.enums.PaymentMethod;
import lombok.Data;

import java.util.Date;

@Data
public class BankTransaction {

    private String OperationId;
    private String shopId;
    private String label;
    private String cardId;
    private String checkToken;
    private float amount;
    private Date date;
    private PaymentMethod paymentMethod;
}

