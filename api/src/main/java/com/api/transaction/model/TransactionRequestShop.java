package com.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
public class TransactionRequestShop implements Serializable {
    private String id;
    private String token;
    private String name;
    private String sessionId;
    private float amount;

    public TransactionRequestShop(String token, String mac, float amount) {
        this.token = token;
        this.amount = amount;
    }
}
