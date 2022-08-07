package com.bol.lirong.mancala.conn.config;

import com.bol.lirong.mancala.conn.handler.AssignPrincipalHandshakeHandler;
import com.bol.lirong.mancala.settings.MancalaDefault;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Spring websocket configuration with STOMP
 *
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private AssignPrincipalHandshakeHandler assignPrincipalHandshakeHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // add Endpoint, set Handshake handler, with SockJS
        registry.addEndpoint(MancalaDefault.WEBSOCKET_ENDPOINT).setHandshakeHandler(assignPrincipalHandshakeHandler).setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // setup message broker
        registry.enableSimpleBroker(MancalaDefault.GAME_INFO_SUB);
        registry.setApplicationDestinationPrefixes(MancalaDefault.APP_DEST_PREFIX);
        registry.setUserDestinationPrefix(MancalaDefault.USER_DEST_PREFIX);
    }

}
