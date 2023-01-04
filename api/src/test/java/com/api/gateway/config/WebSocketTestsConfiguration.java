package com.api.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.List;

@Configuration
public class WebSocketTestsConfiguration {

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public SockJsClient sockJsClient(WebSocketClient webSocketClient) {
        return new SockJsClient(List.of(new WebSocketTransport(webSocketClient)));
    }

    @Bean
    public WebSocketStompClient stompClient(SockJsClient sockJsClient) {
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new CompositeMessageConverter(List.of(
                new MappingJackson2MessageConverter(),
                new StringMessageConverter(),
                new ByteArrayMessageConverter(),
                new SimpleMessageConverter(),
                new GenericMessageConverter()
        )));
        return stompClient;
    }

}
