package com.api.gateway.handler;

import com.api.gateway.data.DestinationGenerator;
import com.api.gateway.transaction.model.MessageClient;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.tcp.TcpConnection;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.socket.sockjs.client.WebSocketClientSockJsSession;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

public class TpeWebSocketClientTestsHandler implements StompSessionHandler {
    private final AtomicReference<MessageClient> messageClient = new AtomicReference<>();
    private final AtomicReference<String> sessionId = new AtomicReference<>();
    private final DestinationGenerator destinationGenerator = new DestinationGenerator();

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        DefaultStompSession defaultStompSession = (DefaultStompSession) session;
        Field fieldConnection = ReflectionUtils.findField(DefaultStompSession.class, "connection");
        fieldConnection.setAccessible(true);

        String sockjsSessionId = "";
        try {
            TcpConnection<byte[]> connection = (TcpConnection<byte[]>) fieldConnection.get(defaultStompSession);
            try {
                Class adapter = Class.forName("org.springframework.web.socket.messaging.WebSocketStompClient$WebSocketTcpConnectionHandlerAdapter");
                Field fieldSession = ReflectionUtils.findField(adapter, "session");
                fieldSession.setAccessible(true);
                WebSocketClientSockJsSession sockjsSession = (WebSocketClientSockJsSession) fieldSession.get(connection);
                sockjsSessionId = sockjsSession.getId(); // gotcha!
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (StringUtils.isBlank(sockjsSessionId)) {
            throw new IllegalStateException("couldn't extract sock.js session id");
        }

        sessionId.set(sockjsSessionId);

        session.subscribe(
                "/user" + destinationGenerator.getTpeSynchronizationStatusDest(sockjsSessionId),
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

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.out.println(exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return MessageClient.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        MessageClient payloadClient = (MessageClient) payload;
        messageClient.set(payloadClient);
    }

    public MessageClient getMessage() {
        return messageClient.get();
    }

    public String getSessionId() {
        return sessionId.get();
    }
}
