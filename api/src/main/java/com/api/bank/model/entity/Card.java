package com.api.bank.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Card extends Base {

    @Column(nullable=false, unique=true, length=50)
    private String cardId;

    private Date expirationDate;

    public Card() {
        super();
    }
}
