package com.bol.lirong.mancala.conn;

import com.bol.lirong.mancala.conn.handler.AssignPrincipalHandshakeHandler;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.security.Principal;
import java.util.Map;

import static org.mockito.Mockito.verify;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@Slf4j
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class AssignPrincipalHandshakeHandlerTest {

    @Mock
    private ServerHttpRequest serverHttpRequest;

    @Mock
    private WebSocketHandler webSocketHandler;

    @Mock
    private Map<String, Object> attributes;

    @Test
    public void testDetermineUser() {
        //given
        String ATTR_PRINCIPAL = "__principal__";
        AssignPrincipalHandshakeHandler assignPrincipalHandshakeHandler = new AssignPrincipalHandshakeHandler();

        //when
        Principal principal = assignPrincipalHandshakeHandler.determineUser(this.serverHttpRequest,
                this.webSocketHandler,this.attributes);

        //then
        Assertions.assertNotNull(principal);
        Assertions.assertNotNull(principal.getName());
        verify(this.attributes).put(ATTR_PRINCIPAL, principal.getName());
    }
}
