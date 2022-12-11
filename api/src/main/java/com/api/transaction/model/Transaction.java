package com.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String tpeMac;
    private String tpeSerial;
    private String shopName;
    private float amount;
    public TransactionType type;

    public Transaction(String shopName, float amount) {
        this.shopName = shopName;
        this.amount = amount;
    }
}
