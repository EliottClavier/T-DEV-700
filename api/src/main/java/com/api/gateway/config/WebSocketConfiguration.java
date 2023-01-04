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

    // This method is used to register the endpoint that will be used to connect to the WebSocket server
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        /* Endpoints for TPE */
        registry.addEndpoint("/websocket-manager/secured/tpe/socket")
                .setAllowedOrigins("*").withSockJS().setHeartbeatTime(10000);
        /* Endpoints for Shop */
        registry.addEndpoint("/websocket-manager/secured/shop/socket")
                .setAllowedOrigins("*").withSockJS().setHeartbeatTime(25000);
    }

    // List of endpoints accessible by roles when user get a WebSocket session (see SecurityConfig.java)
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

    // This method is used to configure the message broker
    // /topic stands for the public channel
    // /queue stands for the private channel
    // All messages sent to WebSocket server from client must be prefixed with /websocket-manager
    // All subscribed routes must be prefixed with /user
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/websocket-manager");
        config.setUserDestinationPrefix("/user");
    }
}