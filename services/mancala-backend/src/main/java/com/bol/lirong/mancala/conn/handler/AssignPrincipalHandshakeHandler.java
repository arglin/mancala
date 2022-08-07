package com.bol.lirong.mancala.conn.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
@Slf4j
@Component
public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private static final String ATTR_PRINCIPAL = "__principal__";
    @Override
    public Principal determineUser(ServerHttpRequest request, WebSocketHandler webSocketHandler,
                                      Map<String, Object> attributes) {

        //generate a UUID for each connected User(Player) as the Identifier
        //This Identifier is used as the destination of STOMP message for each user
        String playerIdentifier = UUID.randomUUID().toString();
        attributes.put(ATTR_PRINCIPAL, playerIdentifier);

        return () -> playerIdentifier;
    }
}
