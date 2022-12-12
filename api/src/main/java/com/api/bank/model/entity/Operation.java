package com.api.bank.model.entity;

import com.api.bank.model.enums.OperationStatus;
import com.api.bank.model.enums.OperationType;
import com.api.bank.model.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
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
    private float amount;

    private Date operationDate;

    @ManyToOne(cascade = CascadeType.ALL )
    private QrCheck qrCheck;

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

    public Operation(String operationId,
                     String label, float amount,
                     Date operationDate,
                     Account account, QrCheck qrCheck, OperationStatus operationStatus,
                     OperationType operationType, PaymentMethod paymentMethod) {
        super();
        this.operationId = operationId;
        this.label = label;
        this.amount = amount;
        this.operationDate = operationDate;
        this.account = account;
        this.qrCheck = qrCheck;
        this.operationStatus = operationStatus;
        this.operationType = operationType;
        this.paymentMethod = paymentMethod;
    }


}
