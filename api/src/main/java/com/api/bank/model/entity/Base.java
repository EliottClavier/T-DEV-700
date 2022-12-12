package com.api.bank.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;



@MappedSuperclass
@Getter
@Setter
public abstract class Base {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name="id", length=36, columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    @CreatedDate
    @Setter(value = AccessLevel.PRIVATE)
    private Instant createdAt;

    @LastModifiedDate
    @Setter(value = AccessLevel.PRIVATE)
    private Instant modifiedAt;



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
