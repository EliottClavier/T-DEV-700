package com.api.bank.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;


@Entity
public class Card extends Base {

    @Column(nullable=false, unique=true, length=50)
    private String cardId;

    private Date expirationDate;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "account_id")
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
