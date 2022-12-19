package com.api.gateway.transaction.service;

import com.api.gateway.transaction.model.Session;
import com.api.gateway.transaction.model.SessionOrigin;
import com.api.gateway.transaction.model.TransactionRequest;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionRequestService {

    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    public Boolean isShopAlreadyInTransaction(Session session) {
        Gson gson = new Gson();
        AtomicReference<Boolean> present = new AtomicReference<>(false);
        customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).forEach((key, value) -> {
            TransactionRequest transactionRequest = gson.fromJson((String) value, TransactionRequest.class);
            if (transactionRequest.getShopSessionId().equals(session.getSessionId())) {
                present.set(true);
            }
        });
        return present.get();
    }

    public Session deleteTransactionRequestBySessionId(Session session) {
        Gson gson = new Gson();
        Session sessionResponse = new Session();
        // Find transaction either by tpeSessionId or by shopSessionId in Redis
        customRedisTemplate.opsForHash().entries(HASH_KEY_NAME_TRANSACTION).forEach((key, value) -> {
            TransactionRequest transactionRequest = gson.fromJson((String) value, TransactionRequest.class);
            if (transactionRequest.getShopSessionId().equals(session.getSessionId())) {
                customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, key);
                sessionResponse.setSessionId(transactionRequest.getTpeSessionId());
                sessionResponse.setUsername(transactionRequest.getTpeUsername());
                sessionResponse.setOrigin(SessionOrigin.TPE);
            } else if (transactionRequest.getTpeSessionId().equals(session.getSessionId())) {
                customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, key);
                sessionResponse.setSessionId(transactionRequest.getShopSessionId());
                sessionResponse.setUsername(transactionRequest.getShopUsername());
                sessionResponse.setOrigin(SessionOrigin.SHOP);
            }
        });
        return sessionResponse;
    }

    public void updateTransactionRequestRedis(TransactionRequest transactionRequest) {
        customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());
    }

}
