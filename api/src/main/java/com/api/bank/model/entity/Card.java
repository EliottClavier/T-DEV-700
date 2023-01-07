package com.api.bank.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Setter
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Card extends Base {

    @Column(nullable = false, unique = true, length = 50)
    private String cardId;

    private Date expirationDate;

    public Card() {
        super();
        init();
    }

    public Card(String cardId, Date expirationDate) {
        super();
        this.cardId = cardId;
        this.expirationDate = expirationDate;
    }
    public Card(String cardId) {
        super();
        this.cardId = cardId;
        init();
    }

    private void init() {
        if (this.cardId == null) {
            this.cardId = UUID.randomUUID().toString();
        }
        if (this.expirationDate == null) {
            this.expirationDate = setDefaultExpirationDate();
        }
    }

    private Date setDefaultExpirationDate() {
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.add(Calendar.DATE, 2);
        return c.getTime();
    }

    public boolean isExpired() {
        return expirationDate.before(new Date());
    }
}
