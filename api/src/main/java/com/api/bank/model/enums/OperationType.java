package com.api.bank.model.enums;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

public enum OperationType {
    WITHDRAW,
    DEPOSIT,
}
