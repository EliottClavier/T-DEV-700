package com.api.bank.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Operation extends Base {

    @Column(nullable = false, length = 25)
    private String operationId;

    @Column(nullable = false, length = 25)
    private String label;

    @Column(nullable = false)
    private int amount;

    private Date operationDate;

//    @ManyToOne()
//    @JoinColumn(name = "id_report", nullable = false)
//    private Report type;

    @ManyToOne(cascade = CascadeType.ALL)
    private Account account;

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;


    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    public Operation() {
        super();
        if (operationDate == null) {
            operationDate = new Date();
        }
    }

    public Operation( String operationId,
                     String label, int amount,
                     Date operationDate,
                     Account account, OperationStatus operationStatus,
                     OperationType operationType, PaymentMethod paymentMethod) {
        super();
        this.operationId = operationId;
        this.label = label;
        this.amount = amount;
        this.operationDate = operationDate;

        this.account = account;
        this.operationStatus = operationStatus;
        this.operationType = operationType;
        this.paymentMethod = paymentMethod;
    }


}
