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

    private int sold;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Operation> operations;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "card_id")
    private Card card;

    public Account() {
        super();
    }
}
