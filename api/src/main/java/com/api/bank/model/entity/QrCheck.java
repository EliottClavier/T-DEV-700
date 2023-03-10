package com.api.bank.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class QrCheck extends Base {

    @Column(nullable = false, unique = true)
    private String checkToken;
    private double soldAmount;

    @Column(nullable = false)
    private int nbDayOfValidity;

    @OneToMany(mappedBy = "qrCheck", cascade= CascadeType.ALL)
    private List<Operation> operations;

    public QrCheck() {
        super();
        init();
    }

    public QrCheck(double amount, String token) {
        super();
        this.soldAmount = amount;
        this.checkToken = token;
        init();
    }

    public QrCheck(double i, String token, String applicantAccountId) {
        super();
        this.soldAmount = i;
        this.checkToken = token;
        init();
    }

    private void init() {
        if (nbDayOfValidity == 0) {
            nbDayOfValidity = 365;
        }
    }

    public Date getExpirationDate() {
        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + (long) nbDayOfValidity * 24 * 60 * 60 * 1000);
        return expirationDate;
    }

    public boolean isExpired() {
        return getExpirationDate().before(new Date());
    }
    public boolean isEnoughMoney(double amount) {
        return soldAmount >= amount;
    }
}
