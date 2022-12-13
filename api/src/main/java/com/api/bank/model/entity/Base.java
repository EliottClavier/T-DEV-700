package com.api.bank.model.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;


@Getter
@Setter
@MappedSuperclass
public abstract class Base {

    @Id()
    @Column(name = "id", length = 36, columnDefinition = "VARCHAR(36)")
    @Type(type = "uuid-char")
    private UUID id;

    @CreatedDate
    @Setter(value = AccessLevel.PRIVATE)
    private Instant createdAt;

    @LastModifiedDate
    private Instant modifiedAt;

    public Base(UUID id) {
        this.id = id;
        init();
    }

    public Base() {
        init();
    }
    private void init() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (id == null) {
            id = UUID.randomUUID();
        }
    }
}
