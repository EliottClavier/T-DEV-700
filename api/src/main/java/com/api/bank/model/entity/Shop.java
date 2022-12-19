package com.api.bank.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Shop extends Base {

    @NonNull
    private String name;

    @NonNull
    private String password;
    @NonNull
    private Boolean whitelisted;

    public Shop(String name, String password) {
        super();
        this.name = name;
        this.password = password;
        this.whitelisted = false;
    }
}