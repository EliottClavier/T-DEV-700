package com.api.transaction.tpe.controller;

import com.api.transaction.model.*;

import com.api.transaction.tpe.model.TpeManager;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Controller
public class TpeManagerController {

    public static final String HASH_KEY_NAME_TPE = "TPE";
    public static final String HASH_KEY_NAME_TRANSACTION = "TRANSACTION";

    public static final String MESSAGE_PREFIX_SHOP = "/queue/shop";
    public static final String MESSAGE_PREFIX_TPE = "/queue/tpe";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate smt;

    public Message registerTpeRedis(TpeManager tpeManager, Message message) {
        try {
            if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TPE, tpeManager.getId())) {
                message.setMessage("TPE already connected to Redis.");
            } else if (tpeManager.isValid()) {
                customRedisTemplate.opsForHash().put(HASH_KEY_NAME_TPE, tpeManager.getId(), tpeManager.getSessionId());
                message.setMessage("TPE available for transaction.");
                message.setType(MessageType.SUCCESS);
            } else {
                message.setMessage("TPE not valid.");
            }
        } catch (IllegalArgumentException e) {
            message.setMessage("TPE not found.");
        } catch (Exception e) {
            message.setMessage("Error registering TPE.");
        }
        return message;
    }

    public TransactionRequest retrieveTransactionRequest(String id) {
        try {
            Object object = this.customRedisTemplate.opsForHash().get(HASH_KEY_NAME_TRANSACTION, id);
            return new Gson().fromJson(object.toString(), TransactionRequest.class);
        } catch (Exception e) {
            return null;
        }
    }

    public void sendTransactionFailedMessage(
            String tpeId, String tpeSessionId,
            String shopId, String shopSessionId
    ) {
        String destinationTpe = String.format("%s/transaction-failed/%s", MESSAGE_PREFIX_TPE, tpeSessionId);
        String destinationShop = String.format("%s/transaction-failed/%s", MESSAGE_PREFIX_SHOP, shopSessionId);
        this.smt.convertAndSendToUser(
                tpeId, destinationTpe, new Message("Invalid transaction identifier.", MessageType.ERROR)
        );
        this.smt.convertAndSendToUser(
                shopId, destinationShop, new Message("Transaction failed.", MessageType.ERROR)
        );
    }

    public static void realizeTransaction(TransactionRequest transactionRequest) {
        System.out.println("Transaction realized: " + transactionRequest);
    }

    @MessageMapping("/tpe/synchronise")
    public void synchroniseTpeRedis(
            @Payload String tpeString,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        String destination = String.format("%s/synchronised/%s", MESSAGE_PREFIX_TPE, sessionId);
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);
        TpeManager tpeManager = gson.fromJson(tpeString, TpeManager.class);
        message = this.registerTpeRedis(tpeManager, message);
        this.smt.convertAndSendToUser(user.getName(), destination, message);
    }

    @MessageMapping("/tpe/complete-transaction")
    public void completeTransaction(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        String destinationTpe = String.format("%s/transaction-done/%s", MESSAGE_PREFIX_TPE, sessionId);
        String destinationShop = String.format("%s/transaction-done", MESSAGE_PREFIX_SHOP);
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);
        TransactionRequestTpe transactionRequestTpe = gson.fromJson(transactionData, TransactionRequestTpe.class);
        TransactionRequest transactionRequest = retrieveTransactionRequest(transactionRequestTpe.getId());
        TpeManager tpeManager = new TpeManager(transactionRequest.getTpeMac(), sessionId);
        if (this.customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId())) {
            realizeTransaction(transactionRequest);
            this.customRedisTemplate.opsForHash().delete(HASH_KEY_NAME_TRANSACTION, transactionRequest.getId());
            message = this.registerTpeRedis(tpeManager, message);
            this.smt.convertAndSendToUser(tpeManager.getId(), destinationTpe, message);
            this.smt.convertAndSendToUser(
                    transactionRequest.getShopName(),
                    destinationShop + transactionRequest.getShopSessionId(),
                    new Message("Transaction done.", MessageType.SUCCESS)
            );
        } else {
            sendTransactionFailedMessage(
                    user.getName(), sessionId,
                    transactionRequest.getShopName(), transactionRequest.getShopSessionId()
            );
        }
    }

    @MessageMapping("/tpe/cancel-transaction")
    public void cancelTransaction(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        String destinationTpe = String.format("%s/transaction-cancelled/%s", MESSAGE_PREFIX_TPE, sessionId);
        String destinationShop = String.format("%s/transaction-cancelled", MESSAGE_PREFIX_SHOP);
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);
        TransactionRequest transactionRequest = gson.fromJson(transactionData, TransactionRequest.class);
        TpeManager tpeManager = new TpeManager(transactionRequest.getTpeMac(), sessionId);
        message = this.registerTpeRedis(tpeManager, message);
        this.smt.convertAndSendToUser(user.getName(), destinationTpe, message);
        this.smt.convertAndSendToUser(
                transactionRequest.getShopName(),
                destinationShop + transactionRequest.getShopSessionId(),
                new Message("Transaction cancelled.", MessageType.ERROR)
        );
    }

    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        System.out.println("Disconnect event: " + event);
    }
}
