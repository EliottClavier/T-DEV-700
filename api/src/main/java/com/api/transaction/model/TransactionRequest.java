package com.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@RedisHash(value = "TRANSACTION")
public class TransactionRequest implements Serializable {
    private String id;
    private String tpeSessionId;
    private String tpeUsername;
    private String shopSessionId;
    private String shopUsername;

    private float amount;
    public String paymentId;
    public TransactionRequestType type;

    public TransactionRequest(String shopUsername, String shopSessionId, float amount) {
        this.id = UUID.randomUUID().toString();
        this.shopUsername = shopUsername;
        this.shopSessionId = shopSessionId;
        this.amount = amount;
    }

    public Boolean isValid() {
        return id != null && tpeSessionId != null && shopSessionId != null && amount > 0;
    }

    @Override
    public String toString() {
        return String.format("{'id': %s, 'tpeSessionId': %s, 'tpeUsername': %s, 'shopSessionId': %s, 'shopUsername': %s, 'amount': %s, 'type': %s}",
                id, tpeSessionId, tpeUsername, shopSessionId, shopUsername, amount, type);
    }
}
