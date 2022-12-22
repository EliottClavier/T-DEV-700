package com.api.admin.qrcode.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class QrCodeModel {

    private double soldAmount;
    private int nbDayOfValidity;

    public QrCodeModel(double soldAmount, int nbDayOfValidity) {
        this.soldAmount = soldAmount;
        this.nbDayOfValidity = nbDayOfValidity;
    }

    public Date getExpirationDate() {
        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + (long) nbDayOfValidity * 24 * 60 * 60 * 1000);
        return expirationDate;
    }
}
