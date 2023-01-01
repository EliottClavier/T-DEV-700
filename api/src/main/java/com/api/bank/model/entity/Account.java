package com.api.bank.model.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Account extends Base {
    private double sold;

    @OneToMany(mappedBy = "account", cascade=CascadeType.ALL)
    private List<Operation> operations;

    @OneToOne(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "client_id")
    //@Embedded()
    private Client client;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "card_id")
    private Card card;

    public Account() {
        super();
    }

    public Account(double sold, Client client) {
        super();
        this.client = client;
        this.sold = sold;

    }
    public Account(UUID id, double sold, Client client) {
        super(id);
        this.client = client;
        this.sold = sold;
    }
    public Account(double sold, Client client, Card card) {
        super();
        this.client = client;
        this.sold = sold;
        this.card = card;

    }

    public boolean isEnoughMoney(double amount) {
        return sold >= amount;
    }
}
