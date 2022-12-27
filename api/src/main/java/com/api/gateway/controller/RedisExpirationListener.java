package com.api.gateway.controller;

import com.api.gateway.data.DestinationGenerator;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.TransactionRequest;
import com.api.gateway.transaction.model.WebSocketStatus;
import com.api.gateway.transaction.service.TransactionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
public class RedisExpirationListener implements MessageListener {

    private final DestinationGenerator destinationGenerator = new DestinationGenerator();
    private final SimpMessagingTemplate smt;
    private final TransactionRequestService transactionRequestService;
    private final TpeManagerService tpeManagerService;

    @Autowired
    public RedisExpirationListener(
            SimpMessagingTemplate smt,
            TransactionRequestService transactionRequestService,
            TpeManagerService tpeManagerService
    ) {
        this.smt = smt;
        this.transactionRequestService = transactionRequestService;
        this.tpeManagerService = tpeManagerService;
    }


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        if (transactionRequestService.isTransactionRedisKey(key)) {
            String[] keyParts = key.split(":");
            TransactionRequest transactionRequest = transactionRequestService.getTransactionRequestRedisHashByUUID(keyParts[1]);
            if (transactionRequest != null) {
                transactionRequestService.deleteTransactionRequestRedisHashByUUID(transactionRequest.getId());

                // Add TPE to Redis
                TpeManager tpeManager = new TpeManager(transactionRequest.getTpeUsername(), transactionRequest.getTpeSessionId());
                com.api.gateway.transaction.model.Message messageSync = tpeManagerService.addTpeRedis(tpeManager);

                // Send transaction timed out message to TPE
                smt.convertAndSendToUser(
                        transactionRequest.getTpeUsername(),
                        destinationGenerator.getTpeTransactionStatusDest(transactionRequest.getTpeSessionId()),
                        new com.api.gateway.transaction.model.Message(
                                "Transaction timed out. TPE's status after synchronising again: : " + messageSync.toString(),
                                WebSocketStatus.TRANSACTION_TIMED_OUT
                        )
                );

                // Send transaction timed out message to Shop
                smt.convertAndSendToUser(
                        transactionRequest.getShopUsername(),
                        destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                        new com.api.gateway.transaction.model.Message(
                                "Transaction timed out.", WebSocketStatus.TRANSACTION_TIMED_OUT
                        )
                );
            }
        }
    }
}
