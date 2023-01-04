package com.api.gateway.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * TransactionRequestTpe class used to receive transaction request infos from TPE
 */
@Data
@AllArgsConstructor
public class TransactionRequestTpe implements Serializable {
    private String paymentId;
    private String type;
}
