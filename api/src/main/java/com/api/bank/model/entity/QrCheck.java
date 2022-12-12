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


    private String checkToken;


    private int soldAmount;


    private int nbDayOfValidity;

    @OneToMany(mappedBy = "qrCheck", cascade= CascadeType.ALL)
    private List<Operation> operations;

    public QrCheck() {
        super();
    }

    public Date getExpirationDate() {
        Date expirationDate = new Date();
        expirationDate.setTime(expirationDate.getTime() + (long) nbDayOfValidity * 24 * 60 * 60 * 1000);
        return expirationDate;
    }
}
