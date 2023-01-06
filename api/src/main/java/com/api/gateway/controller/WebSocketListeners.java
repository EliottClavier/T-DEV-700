package com.api.gateway.controller;

import com.api.gateway.data.DestinationGenerator;
import com.api.gateway.tpe.model.TpeManager;
import com.api.gateway.tpe.service.TpeManagerService;
import com.api.gateway.transaction.model.*;
import com.api.gateway.transaction.service.TransactionRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Controller
public class WebSocketListeners {

    private final DestinationGenerator destinationGenerator = new DestinationGenerator();

    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private TpeManagerService tpeManagerService;

    @Autowired
    private TransactionRequestService transactionRequestService;

    /**
     * Websocket event listener for when a user disconnects
     * If the TPE disconnects, it sends message to the Shop on /user/queue/shop/transaction-status/{sessionId}
     * If the Shop disconnects, it sends message to the TPE  on /user/queue/tpe/synchronization-status/{sessionId}
     *
     * The sessionId retrieved by @param event is the session id of the user that disconnected
     * That's why this method also retrieves the sessionId of the other user impacted by the transaction
     *
     * @param event the event sent by the websocket
     */
    @EventListener
    public void onSocketDisconnected(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (tpeManagerService.isTpeRedisAvailable(Objects.requireNonNull(sha.getUser()).getName())) {
            tpeManagerService.deleteTpeRedisByTpeUsername(sha.getUser().getName());
        }

        Session session = transactionRequestService.deleteTransactionRequestBySessionId(
                new Session(Objects.requireNonNull(sha.getUser()).getName(), Objects.requireNonNull(sha.getSessionId()))
        );

        if (session.isComplete()) {
            if (session.getOrigin().equals(SessionOrigin.SHOP)) {
                smt.convertAndSendToUser(
                        session.getUsername(),
                        destinationGenerator.getShopTransactionStatusDest(session.getSessionId()),
                        new Message("Transaction cancelled after losing connection with TPE.", WebSocketStatus.LOST_CONNECTION)
                );
            } else if (session.getOrigin().equals(SessionOrigin.TPE)) {
                Message message = tpeManagerService.addTpeRedis(new TpeManager(session.getUsername(), session.getSessionId()));
                smt.convertAndSendToUser(
                        session.getUsername(),
                        destinationGenerator.getTpeSynchronizationStatusDest(session.getSessionId()),
                        new MessageCancelTpe(
                                "Transaction cancelled after losing connection with Shop.",
                                WebSocketStatus.LOST_CONNECTION, message.getType()
                        )
                );
            }
        }
    }

}
