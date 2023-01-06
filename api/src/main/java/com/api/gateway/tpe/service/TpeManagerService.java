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

    /**
     * Used to add TPE to the Redis
     *
     * @param tpeManager the TPE to add
     * @return a Message indicating the status of the operation
     */
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

    /**
     * Method which wraps and calls the addTpeRedis method
     *
     * @param username  the username of the TPE
     * @param sessionId the session id of the TPE
     * @return a Message indicating the status of the operation
     */
    public Message synchronizeTpeRedis(String username, String sessionId) {
        try {
            TpeManager tpeManager = new TpeManager(username, sessionId);
            return this.addTpeRedis(tpeManager);
        } catch (Exception e) {
            return new Message("Error synchronizing TPE.", WebSocketStatus.SYNCHRONIZATION_ERROR);
        }
    }

    /**
     * Used to check if PaymentMethod exist in enum
     *
     * @param paymentMethod the payment method to check
     * @return true if the PaymentMethod exist in enum, false otherwise
     */
    public Boolean isValidPaymentMethod(String paymentMethod) {
        try {
            PaymentMethod.valueOf(paymentMethod);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Used to retrieve a transaction request by TPE session ID
     *
     * @param sessionId the session ID of the TPE
     * @return the transaction request, null if no transaction request found
     */
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

    /**
     * Method to check if a TPE is available for transaction in Redis
     *
     * @param tpeUsername the username of the TPE
     * @return true if the TPE is available for transaction, false otherwise
     */
    public Boolean isTpeRedisAvailable(String tpeUsername) {
        return this.customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeUsername);
    }

    /**
     * Used to delete TPE from the Redis by its username
     *
     * @param tpeUsername the username of the TPE to delete
     */
    public void deleteTpeRedisByTpeUsername(String tpeUsername) {
        this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TPE, tpeUsername);
    }

    /**
     * Used to delete TPE from the Redis by the transaction ID linked to it
     *
     * @param transactionId the transaction ID linked to the TPE to delete
     */
    public void deleteTransactionByTransactionId(String transactionId) {
        customRedisTemplate.delete(HASH_KEY_NAME_TRANSACTION + ":" + transactionId);
    }

    /**
     * Used to retrieve all TPEs available from the Redis
     *
     * @return List of TPEs available, can be empty
     */
    public List<Object> getAllTpeRedis() {
        return customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TPE);
    }

    /**
     * Used to retrieve a random TPE which is available for transaction
     *
     * @return a random TPE available, null if no TPE available
     */
    public Object getRandomTpeRedisUsername() {
        return customRedisTemplate.opsForHash().randomKey(HASH_KEY_NAME_TPE);
    }

    /**
     * Used to retrieve TPE session ID by its username from Redis
     *
     * @param tpeUsername the username of the TPE
     * @return the session ID of the TPE, null if no TPE found
     */
    public Object getTpeRedisSessionIdByUsername(String tpeUsername) {
        return customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TPE, tpeUsername);
    }

}
