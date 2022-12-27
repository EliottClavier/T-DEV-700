package com.api.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /* Endpoints for TPE */
        registry.addEndpoint("/websocket-manager/secured/tpe/socket")
                .setAllowedOrigins("*").withSockJS().setHeartbeatTime(10000);
        /* Endpoints for Shop */
        registry.addEndpoint("/websocket-manager/secured/shop/socket")
                .setAllowedOrigins("*").withSockJS().setHeartbeatTime(25000);
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpDestMatchers("/websocket-manager/tpe/**").hasRole("TPE")
                .simpSubscribeDestMatchers("/user/queue/tpe/**").hasRole("TPE")
                .simpDestMatchers("/websocket-manager/shop/**").hasRole("SHOP")
                .simpSubscribeDestMatchers("/user/queue/shop/**").hasRole("SHOP")
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.DISCONNECT, SimpMessageType.OTHER).permitAll()
                .anyMessage().authenticated();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/websocket-manager");
        config.setUserDestinationPrefix("/user");
    }
}