package com.api.bank.model.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Tpe extends Base {

    @NonNull
    private String mac;

    @NonNull
    private String serial;
    @NonNull
    private Boolean whitelisted;

    public Tpe(String mac, String serial) {
        super();
        this.mac = mac;
        this.serial = serial;
        this.whitelisted = false;
    }
}
