package com.api.bank.model.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardTransaction extends TransactionModel {

    public String cardId;

    public CardTransaction(){
        super();
    }
}
