package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.GameBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@SpringBootTest
public class GameBoardResponseTest {

    @Test
    public void testConstructWithGameBoardForFirstPlayer() {

        //given
        GameBoard gameBoard = new GameBoard();
        int[] firstPits = new int[]{3,5,7,11,3,9};
        int[] secondPits = new int[]{5,3,18,1,3,0};
        gameBoard.setFirstPits(firstPits);
        gameBoard.setSecondPits(secondPits);
        gameBoard.setFirstBigPit(3);
        gameBoard.setSecondBigPit(4);
        boolean isFirstPlayer = true;

        //when
        GameBoardResponse gameBoardResponse = new GameBoardResponse(gameBoard, isFirstPlayer);

        //then
        Assertions.assertArrayEquals(firstPits, gameBoardResponse.getMyPits());
        Assertions.assertArrayEquals(secondPits, gameBoardResponse.getOpponentPits());
        Assertions.assertEquals(gameBoard.getFirstBigPit(), gameBoardResponse.getMyBigPit());
        Assertions.assertEquals(gameBoard.getSecondBigPit(), gameBoardResponse.getOpponentBigPit());
    }

    @Test
    public void testConstructWithGameBoardForSecondPlayer() {

        //given
        GameBoard gameBoard = new GameBoard();
        int[] firstPits = new int[]{13,4,2,6,4,8};
        int[] secondPits = new int[]{5,8,8,2,3,0};
        gameBoard.setFirstPits(firstPits);
        gameBoard.setSecondPits(secondPits);
        gameBoard.setFirstBigPit(5);
        gameBoard.setSecondBigPit(7);
        boolean isFirstPlayer = false;

        //when
        GameBoardResponse gameBoardResponse = new GameBoardResponse(gameBoard, isFirstPlayer);

        //then
        Assertions.assertArrayEquals(firstPits, gameBoardResponse.getOpponentPits());
        Assertions.assertArrayEquals(secondPits, gameBoardResponse.getMyPits());
        Assertions.assertEquals(gameBoard.getFirstBigPit(), gameBoardResponse.getOpponentBigPit());
        Assertions.assertEquals(gameBoard.getSecondBigPit(), gameBoardResponse.getMyBigPit());
    }
}
