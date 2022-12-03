package com.api.bank.model;


import lombok.Data;

import javax.persistence.*;
import java.util.List;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Account extends Base {

    private int sold;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Operation> operations;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "card_id")
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }



    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
//    @OneToOne(mappedBy = "Account", fetch = FetchType.EAGER)
//    @JoinColumn(name = "id")
//    private Card check;

    public Account() {
        super();
    }
}
