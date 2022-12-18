package com.api.bank.model.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Account extends Base {

    private float sold;

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

    public boolean isEnoughMoney(float amount) {
        return sold >= amount;
    }
}
