package com.api.bank.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.Date;

@Data
@Entity
public class Check extends Base {

    @Column(nullable=false, unique=true, length=250)
    private String checkToken;

    private Date expirationDate;

    @OneToOne()
    @Column(name="id", nullable=false)
    private Account account;


}
