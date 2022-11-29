package com.api.bank.model;



import lombok.Data;

import javax.persistence.*;
import java.util.UUID;


@Data
@Entity
public class Client extends Base {

    @Column( nullable=false, length=100)
    private String fistname;
    @Column( nullable=false, length=100)
    private String lastname;

    public Client(UUID id, String fistname, String lastname) {
        super(id);
        this.fistname = fistname;
        this.lastname = lastname;
    }
    @OneToOne( cascade = CascadeType.ALL)
    private Account account;

    public Client() {
        super();
    }
}
