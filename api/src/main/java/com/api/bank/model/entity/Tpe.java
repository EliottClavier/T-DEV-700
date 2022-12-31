package com.api.bank.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Tpe extends Base {

    @Column(nullable = false, unique = true)
    private String androidId;

    @NonNull
    private String password;
    @NonNull
    private Boolean whitelisted;

    public Tpe(String androidId, String password) {
        super();
        this.androidId = androidId;
        this.password = password;
        this.whitelisted = false;
    }
}
