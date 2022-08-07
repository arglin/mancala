package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.GameStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@SpringBootTest
public class GameStatusResponseTest {

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerPENDING() {

        //give
        GameStatus gameStatus = GameStatus.PENDING;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.WAITING_FOR_OPPONENT, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerONGOING_P1() {

        //give
        GameStatus gameStatus = GameStatus.ONGOING_P1;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.MY_TURN, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerONGOING_P2() {

        //give
        GameStatus gameStatus = GameStatus.ONGOING_P2;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.OPPONENT_TURN, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerFINISHED_QUIT_P1() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_QUIT_P1;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.I_QUIT, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerFINISHED_QUIT_P2() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_QUIT_P2;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.OPPONENT_QUIT, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerFINISHED_WON_P1() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_WON_P1;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.WIN, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerFINISHED_WON_P2() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_WON_P2;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.LOSE, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerFINISHED_TIE() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_TIE;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.TIE, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForFirstPlayerFINISHED_CANCEL() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_CANCEL;
        boolean forFirstPlayer = true;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.CANCEL, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerPENDING() {

        //give
        GameStatus gameStatus = GameStatus.PENDING;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.WAITING_FOR_OPPONENT, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerONGOING_P1() {

        //give
        GameStatus gameStatus = GameStatus.ONGOING_P1;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.OPPONENT_TURN, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerONGOING_P2() {

        //give
        GameStatus gameStatus = GameStatus.ONGOING_P2;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.MY_TURN, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerFINISHED_QUIT_P1() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_QUIT_P1;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.OPPONENT_QUIT, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerFINISHED_QUIT_P2() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_QUIT_P1;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.OPPONENT_QUIT, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerFINISHED_WON_P1() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_WON_P1;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.LOSE, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerFINISHED_WON_P2() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_WON_P2;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.WIN, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerFINISHED_TIE() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_TIE;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.TIE, gameStatusResponse);
    }

    @Test
    public void testFromGameStatusToGameStatusResponseForSecondPlayerFINISHED_CANCEL() {

        //give
        GameStatus gameStatus = GameStatus.FINISHED_CANCEL;
        boolean forFirstPlayer = false;
        //when
        GameStatusResponse gameStatusResponse = GameStatusResponse.toGameStatusResponse(gameStatus, forFirstPlayer);
        //then
        Assertions.assertEquals(GameStatusResponse.CANCEL, gameStatusResponse);
    }
}
