package com.api.gateway.tpe.service;

import com.api.bank.model.enums.PaymentMethod;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.WebSocketStatus;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.tpe.model.TpeManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TPE;
import static com.api.gateway.constants.RedisConstants.HASH_KEY_NAME_TRANSACTION;

@Service
public class TpeManagerService {
    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    public Message addTpeRedis(TpeManager tpeManager) {
        Message message = new Message("Processing", WebSocketStatus.SYNCHRONIZATION_ERROR);
        try {
            if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeManager.getUsername())) {
                message.setMessage("TPE already synchronised to Redis.");
                message.setType(WebSocketStatus.ALREADY_SYNCHRONIZED);
            } else if (tpeManager.isValid()) {
                customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TPE, tpeManager.getUsername(), tpeManager.getSessionId());
                message.setMessage("TPE available for transaction.");
                message.setType(WebSocketStatus.SYNCHRONIZED);
            } else {
                message.setMessage("Invalid TPE.");
                message.setType(WebSocketStatus.TPE_INVALID);
            }
        } catch (IllegalArgumentException e) {
            message.setMessage("TPE not found.");
            message.setType(WebSocketStatus.NOT_FOUND);
        } catch (Exception e) {
            message.setMessage("Error registering TPE.");
            message.setType(WebSocketStatus.SYNCHRONIZATION_ERROR);
        }
        return message;
    }

    public Message synchronizeTpeRedis(String username, String sessionId) {
        try {
            TpeManager tpeManager = new TpeManager(username, sessionId);
            return this.addTpeRedis(tpeManager);
        } catch (Exception e) {
            return new Message("Error synchronizing TPE.", WebSocketStatus.SYNCHRONIZATION_ERROR);
        }
    }

    public Boolean isValidPaymentMethod(String paymentMethod) {
        try {
            PaymentMethod.valueOf(paymentMethod);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public TransactionRequest retrieveTransactionRequestByTpeSessionId(String sessionId) {
        // For each transaction return value
        for (String key : customRedisTemplate.keys(HASH_KEY_NAME_TRANSACTION + ":*")) {
            TransactionRequest transactionRequest = new Gson().fromJson(customRedisTemplate.opsForValue().get(key), TransactionRequest.class);
            if (transactionRequest.getTpeSessionId().equals(sessionId)) {
                return transactionRequest;
            }
        }
        return null;
    }

    public Boolean isTpeRedisAvailable(String tpeUsername) {
        return this.customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeUsername);
    }

    public void deleteTpeRedisByTpeUsername(String tpeUsername) {
        this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeUsername);
    }

    public void deleteTransactionByTransactionId(String transactionId) {
        customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + transactionId);
    }

    public List<Object> getAllTpeRedis() {
        return customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TPE);
    }

    public Object getRandomTpeRedisUsername() {
        return customRedisTemplate.opsForHash().randomKey(HASH_KEY_NAME_TPE);
    }

    public Object getTpeRedisSessionIdByUsername(String tpeUsername) {
        return customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TPE, tpeUsername);
    }

}
