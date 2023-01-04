package com.api.gateway.shop.controller;

import com.api.gateway.shop.service.ShopManagerService;
import com.api.gateway.data.DestinationGenerator;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.*;
import com.api.gateway.transaction.service.TransactionRequestService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
public class ShopManagerController {
    private final SimpMessagingTemplate smt;
    private final ShopManagerService shopManagerService;
    private final TpeManagerService tpeManagerService;
    private final TransactionRequestService transactionRequestService;
    private final DestinationGenerator destinationGenerator = new DestinationGenerator();

    @Autowired
    public ShopManagerController(
            SimpMessagingTemplate smt,
            ShopManagerService shopManagerService,
            TpeManagerService tpeManagerService,
            TransactionRequestService transactionRequestService
    ) {
        this.smt = smt;
        this.shopManagerService = shopManagerService;
        this.tpeManagerService = tpeManagerService;
        this.transactionRequestService = transactionRequestService;
    }

    /**
     * Websocket endpoint for Shop pay action
     * It sends message back to the client on /user/queue/shop/transaction-status/{sessionId}
     * It also sends message to related TPE on /user/queue/tpe/transaction-status/{tpeSessionId}
     *
     * @param transactionData the transaction data sent by the Shop (amount)
     * @param user the Shop user that is connected and sending the message
     * @param sessionId the session id of the Shop user sending the message
     */
    @MessageMapping("/shop/pay")
    public void pay(
            @Payload String transactionData,
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        // Get Shop from transaction data
        Gson gson = new Gson();
        TransactionRequestShop transactionRequestShop = gson.fromJson(transactionData, TransactionRequestShop.class);
        try {
            if (shopManagerService.isShopValid(user.getName())) {
                // Create transaction request object from transaction request shop
                TransactionRequest transactionRequest = new TransactionRequest(user.getName(), sessionId, transactionRequestShop.getAmount());
                Session session = new Session(user.getName(), sessionId);
                List<Object> tpeManagers = tpeManagerService.getAllTpeRedis();
                if (!transactionRequestService.isShopAlreadyInTransaction(session)) {
                    if (tpeManagers.size() > 0) {
                        // Get a random TPE to process the transaction
                        Object randomUsername = tpeManagerService.getRandomTpeRedisUsername();
                        if (randomUsername != null) {
                            // Prepare transaction request (choose TPE and register transaction)
                            transactionRequest = shopManagerService.prepareTransactionRequestFromShop(transactionRequest, randomUsername.toString());

                            // Send transaction opened message to TPE
                            smt.convertAndSendToUser(
                                    transactionRequest.getTpeUsername(),
                                    destinationGenerator.getTpeTransactionStatusDest(transactionRequest.getTpeSessionId()),
                                    new TransactionReponsePay(
                                            "Transaction opened.", transactionRequest.getAmount(), WebSocketStatus.TRANSACTION_OPENED
                                    )
                            );

                            // Send transaction opened message to Shop
                            smt.convertAndSendToUser(
                                    user.getName(),
                                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                                    new Message("Transaction opened.", WebSocketStatus.TRANSACTION_OPENED)
                            );
                        } else {
                            smt.convertAndSendToUser(
                                    user.getName(),
                                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                                    new Message("Error while selecting TPE.", WebSocketStatus.NO_TPE_SELECTED)
                            );
                        }
                    } else {
                        smt.convertAndSendToUser(
                                user.getName(),
                                destinationGenerator.getShopTransactionStatusDest(sessionId),
                                new Message("No TPE available.", WebSocketStatus.NO_TPE_FOUND)
                        );
                    }
                } else {
                    smt.convertAndSendToUser(
                            user.getName(),
                            destinationGenerator.getShopTransactionStatusDest(sessionId),
                            new Message("Shop already in a transaction.", WebSocketStatus.ALREADY_IN_TRANSACTION)
                    );
                }
            } else {
                smt.convertAndSendToUser(
                        user.getName(),
                        destinationGenerator.getShopTransactionStatusDest(sessionId),
                        new Message("Shop not found.", WebSocketStatus.NOT_FOUND)
                );
            }
        } catch (Exception e) {
            smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                    new Message("Error while processing transaction.", WebSocketStatus.TRANSACTION_ERROR)
            );
        }
    }

    /**
     * Websocket endpoint for Shop cancel transaction action
     * It sends message back to the client on /user/queue/shop/transaction-status/{sessionId}
     * It also sends message to related TPE on /user/queue/tpe/synchronization-status/{tpeSessionId}
     *
     * @param user the Shop user that is connected and sending the message
     * @param sessionId the session id of the Shop user sending the message
     */
    @MessageMapping("/shop/cancel-transaction")
    public void cancelTransaction(
            Principal user,
            @Header("simpSessionId") String sessionId
    ) {
        TransactionRequest transactionRequest = shopManagerService.retrieveTransactionRequestByShopSessionId(sessionId);
        if (transactionRequest != null) {
            // Send message to TPE
            Message message = tpeManagerService.addTpeRedis(
                    new TpeManager(transactionRequest.getTpeUsername(), transactionRequest.getTpeSessionId())
            );
            smt.convertAndSendToUser(
                    transactionRequest.getTpeUsername(),
                    destinationGenerator.getTpeSynchronizationStatusDest(transactionRequest.getTpeSessionId()),
                    new MessageCancelTpe(
                            "Transaction cancelled.", WebSocketStatus.TRANSACTION_CANCELLED, message.getType()
                    )
            );

            // Send message to Shop
            smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                    new Message("Transaction cancelled.", WebSocketStatus.TRANSACTION_CANCELLED)
            );
        } else {
            smt.convertAndSendToUser(
                    user.getName(),
                    destinationGenerator.getShopTransactionStatusDest(sessionId),
                    new Message("Shop not involved in any transaction.", WebSocketStatus.NOT_INVOLVED)
            );
        }
    }

}
