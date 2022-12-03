package com.api.bank.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class OperationType extends Base {

    @Column( nullable=false, length=25)
    private String label; // Withdraw or Credit...
}
