package com.api.bank.model.enums;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * The operation type can be one of this two states only.
 */
public enum OperationType {
    WITHDRAW,
    DEPOSIT,
}
