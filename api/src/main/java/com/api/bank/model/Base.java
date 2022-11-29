package com.api.bank.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;
import java.util.UUID;

@Data
@Entity
public class Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name="id", nullable=false, unique=true, length=11)
    private UUID id;

    @CreatedDate
    @Setter(value = AccessLevel.PRIVATE)
    private Date CreatedAt;

    public Base(UUID id) {
        this.id = id;
        if(CreatedAt == null) {
            CreatedAt = new Date();
        }
    }
    public Base() {
        if(CreatedAt == null) {
            CreatedAt = new Date();
        }
    }
}
