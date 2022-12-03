package com.api.bank.model.entity;

import lombok.AccessLevel;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;



@MappedSuperclass
public class Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name="id", length=32)
    private UUID id;

    @CreatedDate
    @Setter(value = AccessLevel.PRIVATE)
    private Instant createdAt;


    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @LastModifiedDate
    @Setter(value = AccessLevel.PRIVATE)
    private Instant modifiedAt;

    public UUID getId() {
        return id;
    }

    public Base(UUID id) {
        this.id = id;
        if(createdAt == null) {
            createdAt =  Instant.now();
        }
    }
    public Base() {
        if(createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
