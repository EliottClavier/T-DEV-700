package com.api.bank.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Shop extends Base {

    @Column(nullable = false, unique = true)
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
    public Shop(String id, String name, String password) {
        super(UUID.fromString(id));
        this.name = name;
        this.password = password;
        this.whitelisted = false;
    }
}
