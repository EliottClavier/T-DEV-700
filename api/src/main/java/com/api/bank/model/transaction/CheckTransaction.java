package com.api.bank.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CheckTransaction extends TransactionModel {

    public String checkToken;

    public CheckTransaction(){
        super();
    }
}
