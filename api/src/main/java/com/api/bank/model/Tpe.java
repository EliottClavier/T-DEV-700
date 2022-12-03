package com.api.bank.model;

import lombok.Data;

import javax.persistence.Entity;
import java.util.Date;


@Entity
public class Tpe extends Base {
    private String macId;
    private String TokenRegister;
}
