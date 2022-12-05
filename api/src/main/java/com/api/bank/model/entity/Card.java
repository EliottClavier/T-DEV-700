package com.api.bank.model.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.util.Date;


@Entity
@Data
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
