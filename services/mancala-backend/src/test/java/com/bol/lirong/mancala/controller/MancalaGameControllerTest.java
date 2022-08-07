package com.bol.lirong.mancala.controller;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.exception.InvalidGameStateException;
import com.bol.lirong.mancala.service.MancalaGameService;
import com.bol.lirong.mancala.service.MancalaMessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Principal;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author linlirong
 * @created 02/03/2022
 * @project mancala
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class MancalaGameControllerTest {

    @Mock
    MancalaGameService gameService;

    @Mock
    MancalaMessagingService messagingService;

    private MancalaGameController mancalaGameController;

    @BeforeEach
    void setUp() {
        this.mancalaGameController = new MancalaGameController(this.gameService, this.messagingService);
    }

    @Test
    public void testJoinGameSuccess() {

        //given
        String principalName = UUID.randomUUID().toString();
        Principal principal = () -> principalName;

        //when
        this.mancalaGameController.joinGame(principal);

        //then
        Game game = verify(this.gameService).playerJoin(principalName);
        verify(this.messagingService).syncGameInfo(game);
    }

    @Test
    public void testJoinGameWhenPrincipalIsNUllNeverSyncGame() {

        //given
        //when
        this.mancalaGameController.joinGame(null);

        //then
        verify(this.messagingService, never()).syncGameInfo(null);
    }

    @Test
    public void testSowGameSuccess() {

        //given
        int payload = 4;
        String principalName = UUID.randomUUID().toString();
        Principal principal = () -> principalName;

        //when
        this.mancalaGameController.sow(principal, payload);

        //then
        Game game = verify(this.gameService).playerSow(principalName, payload);
        verify(this.messagingService).syncGameInfo(game);
    }

    @Test
    public void testJSowGameWhenPrincipalIsNUllNeverSyncGame() {

        //given
        //when
        this.mancalaGameController.sow(null, 4);

        //then
        verify(this.messagingService, never()).syncGameInfo(null);
    }

    @Test
    public void testJSowGameWhenPlaySowThrowsInvalidGameStateException() {

        //given
        int payload = 4;
        String principalName = UUID.randomUUID().toString();
        Principal principal = () -> principalName;
        given(this.gameService.playerSow(principal.getName(), payload))
                .willThrow(new InvalidGameStateException("not ", "test_user's", " turn"));

        //when
        this.mancalaGameController.sow(principal, payload);

        //then
        verify(this.messagingService, never()).syncGameInfo(null);
        verify(this.messagingService).syncGameInfo(principalName, "not test_user's turn");
    }

    @Test
    public void testJSowGameWhenPlaySowThrowsInvalidGameInputException() {

        //given
        int payload = 40;
        String principalName = UUID.randomUUID().toString();
        Principal principal = () -> principalName;
        given(this.gameService.playerSow(principal.getName(), payload))
                .willThrow(new InvalidGameStateException("cannot sow at ", payload + ""));

        //when
        this.mancalaGameController.sow(principal, payload);

        //then
        verify(this.messagingService, never()).syncGameInfo(null);
        verify(this.messagingService).syncGameInfo(principalName, "cannot sow at " + payload);
    }
}
