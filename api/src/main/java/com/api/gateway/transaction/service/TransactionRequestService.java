package com.api.gateway.transaction.service;

import com.api.gateway.transaction.model.Session;
import com.api.gateway.transaction.model.SessionOrigin;
import com.api.gateway.transaction.model.TransactionRequest;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_TTL_TRANSACTION;

@Service
public class TransactionRequestService {
    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    /**
     * Used to know if a shop has already asked for a transaction
     *
     * @param session a Session instance containing the shopSessionId and the shopName at this stage
     * @return true if the shop has already asked for a transaction, false otherwise
     */
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

    /**
     * Used to delete a transaction request from Redis by session ID, either TPE or Shop
     * This method is called when a Shop or a TPE disconnects from the websocket while a transaction is processing
     *
     * @param session a Session instance containing the shopSessionId and the shopName at this stage
     * @return Session instance containing the shopSessionId, the shopName and the SessionOrigin
     * which allows to know which route to call from the websocket to announce that the transaction has been cancelled
     * by the Shop / TPE linked by the transaction
     */
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

    /**
     * Used to know if a redis key who expired is a transaction request
     *
     * @param key the key which expired in Redis
     * @return true if the key indicates a transaction request, false otherwise
     */
    public Boolean isTransactionRedisKey(String key) {
        return key.startsWith(HASH_KEY_NAME_TRANSACTION + ":");
    }

    /**
     * Used to delete a transaction request from Redis by transaction ID
     *
     * @param uuid the uuid of the transaction request to delete
     */
    public void deleteTransactionRequestRedisHashByUUID(String uuid) {
        customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, uuid);
        customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + uuid);
    }

    /**
     * Used to retrieve a transaction request from Redis by transaction ID
     *
     * @param uuid the uuid of the transaction request to retrieve
     * @return a TransactionRequest instance
     */
    public TransactionRequest getTransactionRequestRedisHashByUUID(String uuid) {
        Gson gson = new Gson();
        return gson.fromJson((String) customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TRANSACTION, uuid), TransactionRequest.class);
    }

    /**
     * Used to set or update a transaction request in Redis, both in value and hash
     * The value holds the transaction UUID and the expiration time
     * The hash holds the transaction UUID and the transaction request as a JSON string
     *
     * @param transactionRequest the uuid of the transaction request to set or update
     */
    public void updateTransactionRequestRedis(TransactionRequest transactionRequest) {
        customRedisTemplate.opsForValue().set(
                HASH_KEY_NAME_TRANSACTION + ":" + transactionRequest.getId(), transactionRequest.toString(),
                HASH_KEY_TTL_TRANSACTION, TimeUnit.MINUTES
        );
        customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId(), transactionRequest.toString());
    }

}
