package com.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TransactionRequestTpe implements Serializable {
    private String paymentId;
    private String type;
}
