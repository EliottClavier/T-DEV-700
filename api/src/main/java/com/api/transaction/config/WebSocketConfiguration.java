package com.api.transaction.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /* Endpoints for TPE */
        registry.addEndpoint("/websocket-manager/secured/tpe/socket").setAllowedOrigins("*");
        registry.addEndpoint("/websocket-manager/secured/tpe/socket").setAllowedOrigins("*").withSockJS();
        /* Endpoints for Shop */
        registry.addEndpoint("/websocket-manager/secured/shop/socket").setAllowedOrigins("*");
        registry.addEndpoint("/websocket-manager/secured/shop/socket").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/websocket-manager");
        config.setUserDestinationPrefix("/user");
    }

}