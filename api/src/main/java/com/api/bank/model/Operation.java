package com.api.bank.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
public class Operation extends Base {

    @Column( nullable=false, length=25)
    private String operationId;

    @Column( nullable=false, length=25)
    private String label;

    @Column( nullable=false)
    private int amount;

    private Date operationDate;

    @ManyToOne()
    @JoinColumn(name = "id")
    private Report type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;

    @ManyToOne()
    @JoinColumn(name = "id")
    private OperationType operationType;

    public Operation() {
        super();
        if(operationDate == null) {
            operationDate = new Date();
        }
    }
}
