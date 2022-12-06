package com.api.bank.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

public enum OperationType {
    WITHDRAW,
    CREDIT
}
