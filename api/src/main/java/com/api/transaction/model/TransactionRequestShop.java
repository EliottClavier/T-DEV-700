package com.api.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TransactionRequestShop implements Serializable {
    private float amount;
}
