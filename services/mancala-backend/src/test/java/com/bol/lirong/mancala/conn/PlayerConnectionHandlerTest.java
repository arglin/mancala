package com.bol.lirong.mancala.conn;

import com.bol.lirong.mancala.conn.handler.PlayerConnectionHandler;
import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.service.MancalaGameService;
import com.bol.lirong.mancala.service.MancalaMessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class PlayerConnectionHandlerTest {

    @Mock
    private MancalaGameService gameService;

    @Mock
    private MancalaMessagingService messagingService;

    @Mock
    private SessionConnectEvent sessionConnectEvent;

    @Mock
    private SessionDisconnectEvent sessionDisconnectEvent;

    private PlayerConnectionHandler playerConnectionHandler;


    @BeforeEach
    void setUp() {
        this.playerConnectionHandler = new PlayerConnectionHandler(this.gameService, this.messagingService);
    }

    @Test
    public void testHandleConnectEvent() {

        //given
        String playerId = UUID.randomUUID().toString();
        given(this.sessionConnectEvent.getUser()).willReturn(() -> playerId);

        //when
        this.playerConnectionHandler.handleConnectEvent(sessionConnectEvent);

        //then
        verify(this.gameService).playerConnected(playerId);
        verify(this.messagingService, never()).syncGameInfo(new Game());
    }

    @Test
    public void testHandleDisconnectEvent() {

        //given
        String playerId = UUID.randomUUID().toString();
        Game game = new Game();
        given(this.sessionDisconnectEvent.getUser()).willReturn(() -> playerId);
        given(this.gameService.playerDisConnected(playerId)).willReturn(game);

        //when
        this.playerConnectionHandler.handleDisconnectEvent(sessionDisconnectEvent);

        //then
        verify(this.gameService).playerDisConnected(playerId);
        verify(this.messagingService).syncGameInfo(game);
    }
}
