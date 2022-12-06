package com.api.bank.model.entity;



import com.api.bank.model.enums.SocialReasonStatus;
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
    private SocialReasonStatus socialReason;

    @Column( unique = true, length=100)
    private String organisationName;
    @Column( length=100)
    private String firstname;
    @Column( length=100)
    private String lastname;

    @OneToOne(mappedBy = "client", orphanRemoval = true)
    private Account account;

    public Client(UUID id, String firstname, String lastname, SocialReasonStatus socialReasonStatus) {
        super(id);
        this.socialReason = socialReasonStatus;
        this.firstname = firstname;
        this.lastname = lastname;


    }
    public Client(UUID id, String organisationName, SocialReasonStatus socialReasonStatus) {
        super(id);
        this.socialReason = socialReasonStatus;
        this.firstname = firstname;
        this.lastname = lastname;
        this.organisationName = organisationName;


    }

    public Client() {
        super();
    }
}
