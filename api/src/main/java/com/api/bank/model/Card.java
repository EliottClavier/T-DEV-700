package com.api.bank.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Card extends Base {

    @Column(nullable=false, unique=true, length=50)
    private String cardId;

    private Date expirationDate;

    @OneToOne(mappedBy = "card", orphanRemoval = true)
    private Account account;



    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Card() {
        super();
    }
}
