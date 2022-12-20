package com.api.bank.model.transaction;

import com.api.bank.model.enums.TransactionStatus;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TransactionResult {
    private TransactionStatus transactionStatus;
    private String transactionId;
    private String message;

    public TransactionResult(TransactionStatus transactionStatus, String transactionId, String message){
        super();
        this.transactionStatus = transactionStatus;
        this.transactionId = transactionId;
        this.message = message;
    }


}
