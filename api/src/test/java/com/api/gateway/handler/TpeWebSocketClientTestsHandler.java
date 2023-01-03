package com.api.gateway.handler;

import com.api.gateway.transaction.model.MessageClient;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;

public class TpeWebSocketClientTestsHandler extends WebSocketClientTestsHandler {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        super.afterConnected(session, connectedHeaders);

        session.subscribe(
                "/user" + destinationGenerator.getTpeSynchronizationStatusDest(sessionId.get()),
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageClient.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageClient payloadClient = (MessageClient) payload;
                        messageClient.set(payloadClient);
                    }
                }
        );

        session.subscribe(
                "/user" + destinationGenerator.getTpeTransactionStatusDest(sessionId.get()),
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageClient.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageClient payloadClient = (MessageClient) payload;
                        messageClient.set(payloadClient);
                    }
                }
        );

        session.subscribe(
                destinationGenerator.getServerStatusDest(),
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return MessageClient.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        MessageClient payloadClient = (MessageClient) payload;
                        messageClient.set(payloadClient);
                    }
                }
        );
    }
}
