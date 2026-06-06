package com.careflow.hms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${websocket.broker.type:simple}") 
    private String brokerType; 
 
    @Value("${websocket.broker.host:localhost}") 
    private String brokerHost; 
 
    @Value("${websocket.broker.port:61613}") 
    private int brokerPort; 

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        if ("redis".equals(brokerType)) { 
            // Redis pub/sub for production — enables horizontal scaling 
            config.enableStompBrokerRelay("/topic", "/queue") 
                .setRelayHost(brokerHost) 
                .setRelayPort(brokerPort) 
                .setSystemLogin("admin") 
                .setSystemPasscode("admin") 
                .setClientLogin("guest") 
                .setClientPasscode("guest"); 
        } else { 
            // Simple in-memory broker for development 
            config.enableSimpleBroker("/topic", "/queue"); 
        } 
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
