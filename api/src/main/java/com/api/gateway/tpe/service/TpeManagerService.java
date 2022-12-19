package com.api.gateway.tpe.service;

import com.api.bank.model.enums.PaymentMethod;
import com.api.gateway.transaction.model.Message;
import com.api.gateway.transaction.model.MessageType;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.tpe.model.TpeManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TpeManagerService {

    public static final String HASH_KEY_NAME_TPE = "TPE";
    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    public Message addTpeRedis(TpeManager tpeManager) {
        Message message = new Message("Processing", MessageType.SYNCHRONIZATION_ERROR);
        try {
            if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeManager.getUsername())) {
                message.setMessage("TPE already synchronised to Redis.");
                message.setType(MessageType.ALREADY_SYNCHRONIZED);
            } else if (tpeManager.isValid()) {
                customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TPE, tpeManager.getUsername(), tpeManager.getSessionId());
                message.setMessage("TPE available for transaction.");
                message.setType(MessageType.SYNCHRONIZED);
            } else {
                message.setMessage("Invalid TPE.");
                message.setType(MessageType.TPE_INVALID);
            }
        } catch (IllegalArgumentException e) {
            message.setMessage("TPE not found.");
            message.setType(MessageType.NOT_FOUND);
        } catch (Exception e) {
            message.setMessage("Error registering TPE.");
            message.setType(MessageType.SYNCHRONIZATION_ERROR);
        }
        return message;
    }

    public Message synchronizeTpeRedis(String username, String sessionId) {
        try {
            TpeManager tpeManager = new TpeManager(username, sessionId);
            return this.addTpeRedis(tpeManager);
        } catch (Exception e) {
            return new Message("Error synchronizing TPE.", MessageType.SYNCHRONIZATION_ERROR);
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
        for (Object transaction : customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TRANSACTION)) {
            TransactionRequest transactionRequest = new Gson().fromJson((String) transaction, TransactionRequest.class);
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
        this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, transactionId);
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
