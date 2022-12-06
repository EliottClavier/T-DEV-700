package com.api.bank.model.transaction;

import lombok.Data;

import java.util.Date;

@Data
public abstract class TransactionModel {

    public String OperationId;
    public int amount;
    public String tokenShop;
    public String tokenTpe;
    public Date date;
}

