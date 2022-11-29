package com.api.bank.model;


import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Account extends Base {

    private int sold;

    @OneToMany(mappedBy = "account", cascade = CascadeType.REMOVE)
    private List<Operation> operations;

    @OneToOne(mappedBy ="account", fetch = FetchType.EAGER)
    private Card card;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private Client client;

//    @OneToOne(mappedBy = "Account", fetch = FetchType.EAGER)
//    @JoinColumn(name = "id")
//    private Card check;

    public Account() {
        super();
    }
}
