package com.api.transaction.tpe.controller;

import com.api.transaction.data.DestinationGenerator;
import com.api.transaction.model.*;

import com.api.transaction.tpe.model.TpeManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TpeManagerController {

    public static final String HASH_KEY_NAME_TPE = "TPE";
    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    private final DestinationGenerator destinationGenerator = new DestinationGenerator();

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate smt;

    public Message registerTpeRedis(TpeManager tpeManager) {
        Message message = new Message("Processing", MessageType.SYNCHRONIZATION_ERROR);
        try {
            if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeManager.getId())) {
                message.setMessage("TPE already synchronised to Redis.");
                message.setType(MessageType.ALREADY_SYNCHRONIZED);
            } else if (tpeManager.isValid()) {
                customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TPE, tpeManager.getId(), tpeManager.getSessionId());
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

    public TransactionRequest retrieveTransactionRequestByTpeSessionId(String sessionId) {
        // For each transaction return value
        for (Object transaction : customRedisTemplate.opsForHash().values(HASH_KEY_NAME_TRANSACTION)) {
            TransactionRequest transactionRequest = new Gson().fromJson((String) transaction, TransactionRequest.class);
            System.out.println(transactionRequest);
            if (transactionRequest.getTpeSessionId().equals(sessionId)) {
                return transactionRequest;
            }
        }
        return null;
    }

    public static void realizeTransaction(TransactionRequest transactionRequest) {
        System.out.println("Transaction realized: " + transactionRequest);
    }

    @MessageMapping("/tpe/synchronize")
    public void synchronizeTpeRedis(
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        try {
            TpeManager tpeManager = new TpeManager(user.getName(), sessionId);
            Message message = this.registerTpeRedis(tpeManager);
            this.smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getTpeSynchronizationStatusDest(sessionId),
                    message
            );
        } catch (Exception e) {
            this.smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getTpeSynchronizationStatusDest(sessionId),
                    new Message("Error synchronising TPE.", MessageType.SYNCHRONIZATION_ERROR)
            );
        }
    }

    @MessageMapping("/tpe/complete-transaction")
    public void completeTransaction(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        if (!this.customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, user.getName())) {
            Gson gson = new Gson();
            TransactionRequestTpe transactionRequestTpe = gson.fromJson(transactionData, TransactionRequestTpe.class);
            TransactionRequest transactionRequest = retrieveTransactionRequestByTpeSessionId(sessionId);
            if (transactionRequest != null) {
                // Complete transaction request with new data
                transactionRequest.setType(TransactionRequestType.valueOf(transactionRequestTpe.getType()));
                transactionRequest.setPaymentId(transactionRequestTpe.getPaymentId());

                // Execute transaction and remove it from Redis
                realizeTransaction(transactionRequest);
                this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId());

                // Make the Tpe available for a new transaction
                TpeManager tpeManager = new TpeManager(user.getName(), sessionId);
                Message message = this.registerTpeRedis(tpeManager);

                // Send transaction done message to TPE
                this.smt.convertAndSendToUser(
                        tpeManager.getId(),
                        destinationGenerator.getTpeTransactionStatusDest(sessionId),
                        message
                );

                // Send transaction done message to Shop
                this.smt.convertAndSendToUser(
                        transactionRequest.getShopUsername(),
                        destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                        new Message("Transaction done.", MessageType.TRANSACTION_DONE)
                );
            } else {
                this.smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getTpeTransactionStatusDest(sessionId),
                        new Message("TPE not involved in any transaction.", MessageType.NOT_INVOLVED)
                );
            }
        } else {
            this.smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getTpeTransactionStatusDest(sessionId),
                    new Message("TPE still marked available while trying to complete a transaction.", MessageType.STILL_AVAILABLE)
            );
        }
    }

    @MessageMapping("/tpe/cancel-transaction")
    public void cancelTransaction(
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        if (!this.customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, user.getName())) {
            TransactionRequest transactionRequest = retrieveTransactionRequestByTpeSessionId(sessionId);
            if (transactionRequest != null) {
                TpeManager tpeManager = new TpeManager(user.getName(), sessionId);
                Message message = this.registerTpeRedis(tpeManager);

                // Send message to TPE
                this.smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getTpeSynchronizationStatusDest(sessionId),
                        message
                );

                // Send message to Shop
                this.smt.convertAndSendToUser(
                        transactionRequest.getShopUsername(),
                        destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                        new Message("Transaction cancelled.", MessageType.TRANSACTION_CANCELLED)
                );
            } else {
                this.smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getShopTransactionStatusDest(sessionId),
                        new Message("TPE not involved in any transaction.", MessageType.NOT_INVOLVED)
                );
            }
        } else {
            this.smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                    new Message("TPE still marked available while trying to cancel a transaction.", MessageType.STILL_AVAILABLE)
            );
        }
    }
}
