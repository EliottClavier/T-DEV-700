package com.api.bank.model.entity;



import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;



@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Client extends Base {

    @Column( nullable=false, length=100)
    private String fistname;
    @Column( nullable=false, length=100)
    private String lastname;

    @OneToOne(mappedBy = "client", orphanRemoval = true)
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Client(UUID id, String fistname, String lastname) {
        super(id);
        this.fistname = fistname;
        this.lastname = lastname;
    }


    public Client() {
        super();
    }
}
