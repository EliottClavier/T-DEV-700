package com.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@RedisHash(value = "TRANSACTION")
public class TransactionRequest implements Serializable {
    private String id;
    private String tpeMac;
    private String shopName;
    private String shopId;
    private String shopSessionId;
    private float amount;
    public TransactionRequestType type;

    public TransactionRequest(TransactionRequestShop transactionRequestShop) {
        this.id = transactionRequestShop.getId();
        this.shopId = transactionRequestShop.getId();
        this.shopName = transactionRequestShop.getName();
        this.shopSessionId = transactionRequestShop.getSessionId();
        this.amount = transactionRequestShop.getAmount();
    }

    public Boolean isValid() {
        return shopId != null && shopName != null && shopSessionId != null && amount > 0 && id != null;
    }

    @Override
    public String toString() {
        return String.format("{'id': %s, 'tpeMac': %s, 'shopName': %s, 'shopId': %s, 'shopSessionId': %s, 'amount': %s, 'type': %s}",
                id, tpeMac, shopName, shopId, shopSessionId, amount, type);
    }
}
