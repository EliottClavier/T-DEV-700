package com.api.bank.model.transaction;

import lombok.Data;

@Data
public class TransactionResult {
    public TransactionStatus transactionStatus;
    public String transactionId;
    public String message;

    public TransactionResult(TransactionStatus transactionStatus,String transactionId,String message){
        super();
        this.transactionStatus = transactionStatus;
        this.transactionId = transactionId;
        this.message = message;
    }


}
