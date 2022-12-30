package com.api.admin.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class QrCodeModel {

    private double amount;
    private UUID clientId;

    public QrCodeModel(double amount, String clientId) {
        this.amount = amount;
        this.clientId = UUID.fromString(clientId);
    }
}
