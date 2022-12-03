package com.api.bank.model.entity;

import java.util.Date;


//@Entity
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Check extends Base {

//    @Column(name = "token")
    private String checkToken;

//    @Column(name = "sold_amt")
    private int soldAmount;

//    @Column(name = "expi_date")
    private Date expirationDate;

//    @OneToOne(orphanRemoval = true)
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account;


}
