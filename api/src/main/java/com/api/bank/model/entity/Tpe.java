package com.api.bank.model.entity;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Tpe extends Base {
    private String macId;
    private String TokenRegister;
}
