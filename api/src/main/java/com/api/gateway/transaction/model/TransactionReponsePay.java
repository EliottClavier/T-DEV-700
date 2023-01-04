package com.api.gateway.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * TransactionResponsePay used to send transaction data useful for payment by TPE
 */
@Data
@Getter
@AllArgsConstructor
public class TransactionReponsePay {
    private String message;
    private float amount;
    private Object type;
}
