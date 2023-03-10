package com.api.gateway.transaction.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

/**
 * TransactionRequestShop class used to receive transaction request from shop
 */
@Data
@AllArgsConstructor
public class TransactionRequestShop implements Serializable {
    private float amount;
}
