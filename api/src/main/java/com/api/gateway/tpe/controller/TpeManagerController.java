package com.api.gateway.tpe.controller;

import com.api.bank.manager.BankManager;
import com.api.bank.manager.IBankManager;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.transaction.BankTransaction;
import com.api.bank.model.transaction.TransactionResult;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.data.DestinationGenerator;
import com.api.gateway.transaction.model.*;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class TpeManagerController {

    private final DestinationGenerator destinationGenerator = new DestinationGenerator();

    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private TpeManagerService tpeManagerService;

    @Autowired
    private IBankManager bankManager;

    public static void realizeTransaction(TransactionRequest transactionRequest) {
        System.out.println("Transaction realized: " + transactionRequest);
    }

    @MessageMapping("/tpe/synchronize")
    public void synchronizeTpeRedis(
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        smt.convertAndSendToUser(
                user.getName(),
                destinationGenerator.getTpeSynchronizationStatusDest(sessionId),
                tpeManagerService.synchronizeTpeRedis(user.getName(), sessionId)
        );
    }

    @MessageMapping("/tpe/complete-transaction")
    public void completeTransaction(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        if (!tpeManagerService.isTpeRedisAvailable(user.getName())) {
            Gson gson = new Gson();
            TransactionRequestTpe transactionRequestTpe = gson.fromJson(transactionData, TransactionRequestTpe.class);
            TransactionRequest transactionRequest = tpeManagerService.retrieveTransactionRequestByTpeSessionId(sessionId);
            if (transactionRequest != null) {
                if (tpeManagerService.isValidPaymentMethod(transactionRequestTpe.getType())) {
                    // Complete transaction request with new data
                    transactionRequest.setPaymentMethod(PaymentMethod.valueOf(transactionRequestTpe.getType()));
                    transactionRequest.setPaymentId(transactionRequestTpe.getPaymentId());

                    // Execute transaction
                    BankTransaction transaction = new BankTransaction(
                        transactionRequest.getId(),
                        transactionRequest.getShopUsername(),
                        transactionRequest.getShopUsername(),
                        transactionRequest.getPaymentId(),
                        transactionRequest.getAmount(),
                        transactionRequest.getPaymentMethod()
                    );

                    TransactionResult transactionResult = bankManager.HandleTransaction(transaction);

                    if (transactionResult.getTransactionStatus() == TransactionStatus.SUCCESS) {
                        // Remove transaction from Redis
                        tpeManagerService.deleteTransactionByTransactionId(transactionRequest.getId());

                        // Make the Tpe available for a new transaction
                        TpeManager tpeManager = new TpeManager(user.getName(), sessionId);
                        tpeManagerService.addTpeRedis(tpeManager);

                        // Send transaction done message to TPE
                        smt.convertAndSendToUser(
                                tpeManager.getUsername(),
                                destinationGenerator.getTpeTransactionStatusDest(sessionId),
                                new Message(transactionResult.getMessage(), transactionResult.getTransactionStatus())
                        );

                        // Send transaction done message to Shop
                        smt.convertAndSendToUser(
                                transactionRequest.getShopUsername(),
                                destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                                new Message(transactionResult.getMessage(), transactionResult.getTransactionStatus())
                        );
                    } else {
                        tpeManagerService.deleteTransactionByTransactionId(transactionRequest.getId());
                        // Send transaction error message to TPE
                        smt.convertAndSendToUser(
                                user.getName(),
                                destinationGenerator.getTpeTransactionStatusDest(sessionId),
                                new Message(transactionResult.getMessage(), transactionResult.getTransactionStatus())
                        );
                        // Send transaction error message to Shop
                        smt.convertAndSendToUser(
                                transactionRequest.getShopUsername(),
                                destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                                new Message("Error while processing transaction.", TransactionStatus.FAILED)
                        );
                    }
                } else {
                    tpeManagerService.deleteTransactionByTransactionId(transactionRequest.getId());
                    // Send transaction error message to TPE
                    smt.convertAndSendToUser(
                            user.getName(),
                            destinationGenerator.getTpeTransactionStatusDest(sessionId),
                            new Message("Invalid payment method.", WebSocketStatus.INVALID_PAYMENT_METHOD)
                    );
                    // Send transaction error message to Shop
                    smt.convertAndSendToUser(
                            transactionRequest.getShopUsername(),
                            destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                            new Message("Error while processing transaction.", WebSocketStatus.TRANSACTION_ERROR)
                    );
                }
            } else {
                smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getTpeTransactionStatusDest(sessionId),
                        new Message("TPE not involved in any transaction.", WebSocketStatus.NOT_INVOLVED)
                );
            }
        } else {
            smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getTpeTransactionStatusDest(sessionId),
                    new Message("TPE still marked available while trying to complete a transaction.", WebSocketStatus.STILL_AVAILABLE)
            );
        }
    }

    @MessageMapping("/tpe/cancel-transaction")
    public void cancelTransaction(
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        if (!tpeManagerService.isTpeRedisAvailable(user.getName())) {
            TransactionRequest transactionRequest = tpeManagerService.retrieveTransactionRequestByTpeSessionId(sessionId);
            if (transactionRequest != null) {
                // Send message to TPE
                smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getTpeSynchronizationStatusDest(sessionId),
                        tpeManagerService.addTpeRedis(new TpeManager(user.getName(), sessionId))
                );

                // Send message to Shop
                smt.convertAndSendToUser(
                        transactionRequest.getShopUsername(),
                        destinationGenerator.getShopTransactionStatusDest(transactionRequest.getShopSessionId()),
                        new Message("Transaction cancelled.", WebSocketStatus.TRANSACTION_CANCELLED)
                );
            } else {
                smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getTpeTransactionStatusDest(sessionId),
                        new Message("TPE not involved in any transaction.", WebSocketStatus.NOT_INVOLVED)
                );
            }
        } else {
            smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getTpeTransactionStatusDest(sessionId),
                    new Message("TPE still marked available while trying to cancel a transaction.", WebSocketStatus.STILL_AVAILABLE)
            );
        }
    }
}
