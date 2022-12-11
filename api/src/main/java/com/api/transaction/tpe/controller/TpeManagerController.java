package com.api.transaction.tpe.controller;

import com.api.transaction.model.Message;
import com.api.transaction.model.MessageType;
import com.api.transaction.model.Transaction;
import com.api.transaction.tpe.model.TpeManager;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class TpeManagerController {

    public static final String HASH_KEY_NAME = "TPE";

    @Autowired
    private RedisTemplate<String, String> customRedisTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    public Message registerTpeRedis(TpeManager tpeManager, Message message) {
        try {
            if (customRedisTemplate.opsForHash().hasKey(HASH_KEY_NAME, tpeManager.getId())) {
                message.setMessage("TPE already connected to Redis.");
            } else if (tpeManager.isValid()) {
                customRedisTemplate.opsForHash().put(HASH_KEY_NAME, tpeManager.getId(), tpeManager.getIp());
                message.setMessage("TPE available for transaction.");
                message.setType(MessageType.SUCCESS);
            } else {
                message.setMessage("TPE not valid.");
            }
        } catch (IllegalArgumentException e) {
            message.setMessage("TPE not found.");
        } catch (Exception e) {
            message.setMessage("Error registring TPE.");
        }
        return message;
    }

    @MessageMapping("/tpe/synchronise")
    public void synchroniseTpeRedis(@Payload String tpeString) {
        String responseDestination = "/private/tpe/synchronised";
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);
        TpeManager tpeManager = gson.fromJson(tpeString, TpeManager.class);
        message = this.registerTpeRedis(tpeManager, message);
        this.simpMessagingTemplate.convertAndSendToUser(tpeManager.getId(), responseDestination, message);
    }

    public static void realizeTransaction(Transaction transaction) {
        System.out.println("Transaction realized: " + transaction);
    }

    @MessageMapping("/tpe/complete-transaction")
    public void completeTransaction(@Payload String transactionData) {
        String responseDestinationTpe = "/private/tpe/transaction-done";
        String responseDestinationShop = "/private/shop/transaction-done";
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);
        Transaction transaction = gson.fromJson(transactionData, Transaction.class);
        TpeManager tpeManager = new TpeManager(transaction.getTpeMac(), transaction.getTpeSerial());
        realizeTransaction(transaction);
        message = this.registerTpeRedis(tpeManager, message);
        this.simpMessagingTemplate.convertAndSendToUser(tpeManager.getId(), responseDestinationTpe, message);
        this.simpMessagingTemplate.convertAndSendToUser(
                transaction.getShopName(),
                responseDestinationShop,
                new Message("Transaction done.", MessageType.SUCCESS)
        );
    }

    @MessageMapping("/tpe/cancel-transaction")
    public void cancelTransaction(@Payload String transactionData) {
        String responseDestinationTpe = "/private/tpe/transaction-canceled";
        String responseDestinationShop = "/private/shop/transaction-cancelled";
        Gson gson = new Gson();
        Message message = new Message("Processing", MessageType.ERROR);
        Transaction transaction = gson.fromJson(transactionData, Transaction.class);
        TpeManager tpeManager = new TpeManager(transaction.getTpeMac(), transaction.getTpeSerial());
        message = this.registerTpeRedis(tpeManager, message);
        this.simpMessagingTemplate.convertAndSendToUser(tpeManager.getId(), responseDestinationTpe, message);
        this.simpMessagingTemplate.convertAndSendToUser(
                transaction.getShopName(),
                responseDestinationShop,
                new Message("Transaction cancelled.", MessageType.ERROR)
        );
    }
}
