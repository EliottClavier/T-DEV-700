package com.api.gateway.tpe.controller;

import com.api.bank.manager.IBankManager;
import com.api.bank.model.enums.PaymentMethod;
import com.api.bank.model.enums.TransactionStatus;
import com.api.bank.model.transaction.ShoppingTransactionModel;
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
    private final SimpMessagingTemplate smt;
    private final TpeManagerService tpeManagerService;
    private final IBankManager bankManager;

    @Autowired
    public TpeManagerController(
            SimpMessagingTemplate smt,
            TpeManagerService tpeManagerService,
            IBankManager bankManager
    ) {
        this.smt = smt;
        this.tpeManagerService = tpeManagerService;
        this.bankManager = bankManager;
    }

    /**
     * Websocket endpoint for TPE manager synchronization
     * It sends message back to the client on /user/queue/tpe/synchronization-status/{sessionId}
     *
     * @param user the TPE user that is connected and sending the message
     * @param sessionId the session id of the TPE user sending the message
     */
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

    /**
     * Websocket endpoint for TPE manager complete transaction action
     * It sends message back to the client on /user/queue/tpe/transaction-status/{sessionId}
     * It also sends message to related Shop on /user/queue/shop/transaction-status/{shopSessionId}
     *
     * @param transactionData the transaction data sent by the TPE user (paymentId and paymentMethod)
     * @param user the TPE user that is connected and sending the message
     * @param sessionId the session id of the TPE user sending the message
     */
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
                    ShoppingTransactionModel transaction = new ShoppingTransactionModel(
                        transactionRequest.getId(),
                        transactionRequest.getShopUsername(),
                        transactionRequest.getShopUsername(),
                        transactionRequest.getPaymentId(),
                        transactionRequest.getAmount(),
                        transactionRequest.getPaymentMethod()
                    );

                    TransactionResult transactionResult = bankManager.shoppingTransaction(transaction);

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

    /**
     * Websocket endpoint for TPE manager cancel transaction action
     * On success, it sends message back to the client on /user/queue/tpe/synchronization-status/{sessionId}
     * On failure, it sends message back to the client on /user/queue/tpe/transaction-status/{sessionId}
     * It also sends message to related Shop on /user/queue/shop/transaction-status/{shopSessionId}
     *
     * @param user the TPE user that is connected and sending the message
     * @param sessionId the session id of the TPE user sending the message
     */
    @MessageMapping("/tpe/cancel-transaction")
    public void cancelTransaction(
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        if (!tpeManagerService.isTpeRedisAvailable(user.getName())) {
            TransactionRequest transactionRequest = tpeManagerService.retrieveTransactionRequestByTpeSessionId(sessionId);
            if (transactionRequest != null) {
                // Send message to TPE
                Message message = tpeManagerService.addTpeRedis(new TpeManager(user.getName(), sessionId));
                smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getTpeSynchronizationStatusDest(sessionId),
                        new MessageCancelTpe(
                                "Transaction cancelled.", WebSocketStatus.TRANSACTION_CANCELLED, message.getType()
                        )
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
