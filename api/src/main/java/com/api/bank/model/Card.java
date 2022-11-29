package com.api.bank.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Card extends Base {

    @Column(nullable=false, unique=true, length=50)
    private String cardId;

    private Date expirationDate;

    @OneToOne()
    @Column(name="id", nullable=false)
    private Account account;

    public Card() {
        super();
    }
}
