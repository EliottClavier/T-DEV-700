package com.api.bank.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Operation extends Base {

    @Column( nullable=false, length=25)
    private String operationId;

    @Column( nullable=false, length=25)
    private String label;

    @Column( nullable=false)
    private int amount;

    private Date operationDate;

    @ManyToOne()
    @JoinColumn(name = "id_report", nullable = false)
    private Report type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;

    @ManyToOne()
    @JoinColumn(name = "id_type_operation", nullable = false)
    private OperationType operationType;

    public Operation() {
        super();
        if(operationDate == null) {
            operationDate = new Date();
        }
    }
}
