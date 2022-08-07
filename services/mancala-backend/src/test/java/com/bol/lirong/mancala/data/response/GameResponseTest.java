package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameBoard;
import com.bol.lirong.mancala.data.model.GameStatus;
import com.bol.lirong.mancala.data.model.Player;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.security.RunAs;
import java.util.UUID;

import static org.mockito.Mockito.verify;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@SpringBootTest
public class GameResponseTest {

    @Test
    public void testConstructWithGameBoardForFirstPlayer() {

        //given
        Game game = new Game();

        GameBoard gameBoard = new GameBoard();
        int[] firstPits = new int[]{3, 5, 7, 11, 3, 9};
        int[] secondPits = new int[]{5, 3, 18, 1, 3, 0};
        gameBoard.setFirstPits(firstPits);
        gameBoard.setSecondPits(secondPits);
        gameBoard.setFirstBigPit(3);
        gameBoard.setSecondBigPit(4);

        Player player1 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player1.setGameId(UUID.randomUUID().toString());
        player1.setTotalMatch(34);
        player1.setWinMatch(20);
        player1.setTieMatch(3);

        Player player2 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player2.setGameId(UUID.randomUUID().toString());
        player2.setTotalMatch(17);
        player2.setWinMatch(4);
        player2.setTieMatch(23);

        game.setGameStatus(GameStatus.FINISHED_WON_P1);
        game.setFirstPlayer(player1);
        game.setSecondPlayer(player2);
        game.setGameBoard(gameBoard);

        boolean forFirstPlayer = true;

        //when
        GameResponse gameResponse = new GameResponse(game, forFirstPlayer);

        //then
        //// game board
        Assertions.assertArrayEquals(firstPits, gameResponse.getGameBoard().getMyPits());
        Assertions.assertArrayEquals(secondPits, gameResponse.getGameBoard().getOpponentPits());
        Assertions.assertEquals(3, gameResponse.getGameBoard().getMyBigPit());
        Assertions.assertEquals(4, gameResponse.getGameBoard().getOpponentBigPit());
        ////first player
        Assertions.assertEquals(player1.getPlayerId(), gameResponse.getMe().getPlayerId());
        Assertions.assertEquals(player1.getPlayerName(), gameResponse.getMe().getPlayerName());
        Assertions.assertEquals(player1.getTotalMatch(), gameResponse.getMe().getTotalMatch());
        Assertions.assertEquals(player1.getWinMatch(), gameResponse.getMe().getWinMatch());
        Assertions.assertEquals(player1.getTieMatch(), gameResponse.getMe().getTieMatch());
        ////second player
        Assertions.assertEquals(player2.getPlayerId(), gameResponse.getOpponent().getPlayerId());
        Assertions.assertEquals(player2.getPlayerName(), gameResponse.getOpponent().getPlayerName());
        Assertions.assertEquals(player2.getTotalMatch(), gameResponse.getOpponent().getTotalMatch());
        Assertions.assertEquals(player2.getWinMatch(), gameResponse.getOpponent().getWinMatch());
        Assertions.assertEquals(player2.getTieMatch(), gameResponse.getOpponent().getTieMatch());
        //// game status response
        Assertions.assertEquals(GameStatusResponse.WIN, gameResponse.getGameStatus());
    }

    @Test
    public void testConstructWithGameBoardForSecondPlayer() {

        //given
        Game game = new Game();

        GameBoard gameBoard = new GameBoard();
        int[] firstPits = new int[]{3, 5, 7, 11, 3, 9};
        int[] secondPits = new int[]{5, 3, 18, 1, 3, 0};
        gameBoard.setFirstPits(firstPits);
        gameBoard.setSecondPits(secondPits);
        gameBoard.setFirstBigPit(3);
        gameBoard.setSecondBigPit(4);

        Player player1 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player1.setGameId(UUID.randomUUID().toString());
        player1.setTotalMatch(34);
        player1.setWinMatch(20);
        player1.setTieMatch(3);

        Player player2 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player2.setGameId(UUID.randomUUID().toString());
        player2.setTotalMatch(17);
        player2.setWinMatch(4);
        player2.setTieMatch(23);

        game.setGameStatus(GameStatus.FINISHED_WON_P1);
        game.setFirstPlayer(player1);
        game.setSecondPlayer(player2);
        game.setGameBoard(gameBoard);

        boolean forFirstPlayer = false;

        //when
        GameResponse gameResponse = new GameResponse(game, forFirstPlayer);

        //then
        //// game board
        Assertions.assertArrayEquals(firstPits, gameResponse.getGameBoard().getOpponentPits());
        Assertions.assertArrayEquals(secondPits, gameResponse.getGameBoard().getMyPits());
        Assertions.assertEquals(3, gameResponse.getGameBoard().getOpponentBigPit());
        Assertions.assertEquals(4, gameResponse.getGameBoard().getMyBigPit());
        ////first player
        Assertions.assertEquals(player1.getPlayerId(), gameResponse.getOpponent().getPlayerId());
        Assertions.assertEquals(player1.getPlayerName(), gameResponse.getOpponent().getPlayerName());
        Assertions.assertEquals(player1.getTotalMatch(), gameResponse.getOpponent().getTotalMatch());
        Assertions.assertEquals(player1.getWinMatch(), gameResponse.getOpponent().getWinMatch());
        Assertions.assertEquals(player1.getTieMatch(), gameResponse.getOpponent().getTieMatch());
        ////second player
        Assertions.assertEquals(player2.getPlayerId(), gameResponse.getMe().getPlayerId());
        Assertions.assertEquals(player2.getPlayerName(), gameResponse.getMe().getPlayerName());
        Assertions.assertEquals(player2.getTotalMatch(), gameResponse.getMe().getTotalMatch());
        Assertions.assertEquals(player2.getWinMatch(), gameResponse.getMe().getWinMatch());
        Assertions.assertEquals(player2.getTieMatch(), gameResponse.getMe().getTieMatch());
        //// game status response
        Assertions.assertEquals(GameStatusResponse.LOSE, gameResponse.getGameStatus());
    }
}
