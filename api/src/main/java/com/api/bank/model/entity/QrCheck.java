package com.api.bank.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter

public class QrCheck extends Base {


    private String checkToken;


    private int soldAmount;


    private int nbDayOfValidity;

    public QrCheck() {
        super();
    }

}
