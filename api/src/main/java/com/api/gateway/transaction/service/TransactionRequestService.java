package com.api.gateway.transaction.service;

import com.api.gateway.transaction.model.Session;
import com.api.gateway.transaction.model.SessionOrigin;
import com.api.gateway.transaction.model.TransactionRequest;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class TransactionRequestService {

    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    public static final Long TTl_HASH_KEY = 10L;

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    public Boolean isShopAlreadyInTransaction(Session session) {
        Gson gson = new Gson();
        AtomicReference<Boolean> present = new AtomicReference<>(false);
        customRedisTemplate.keys(HASH_KEY_NAME_TRANSACTION + ":*").forEach(key -> {
            TransactionRequest transactionRequest = gson.fromJson(customRedisTemplate.opsForValue().get(key), TransactionRequest.class);
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
        customRedisTemplate.keys(HASH_KEY_NAME_TRANSACTION + ":*").forEach((key) -> {
            TransactionRequest transactionRequest = gson.fromJson(customRedisTemplate.opsForValue().get(key), TransactionRequest.class);
            if (transactionRequest.getShopSessionId().equals(session.getSessionId())) {
                customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId());
                customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId());
                sessionResponse.setSessionId(transactionRequest.getTpeSessionId());
                sessionResponse.setUsername(transactionRequest.getTpeUsername());
                sessionResponse.setOrigin(SessionOrigin.TPE);
            } else if (transactionRequest.getTpeSessionId().equals(session.getSessionId())) {
                customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId());
                customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId());
                sessionResponse.setSessionId(transactionRequest.getShopSessionId());
                sessionResponse.setUsername(transactionRequest.getShopUsername());
                sessionResponse.setOrigin(SessionOrigin.SHOP);
            }
        });
        return sessionResponse;
    }

    public Boolean isTransactionRedisKey(String key) {
        return key.startsWith(HASH_KEY_NAME_TRANSACTION + ":");
    }

    public void deleteTransactionRequestRedisHashByUUID(String uuid) {
        customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, uuid);
    }

    public TransactionRequest getTransactionRequestRedisHashByUUID(String uuid) {
        Gson gson = new Gson();
        return gson.fromJson((String) customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TRANSACTION, uuid), TransactionRequest.class);
    }

    public void updateTransactionRequestRedis(TransactionRequest transactionRequest) {
        customRedisTemplate.opsForValue().set(
                HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId(), transactionRequest.toString(),
                TTl_HASH_KEY, TimeUnit.MINUTES
        );
        customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());
    }

}
