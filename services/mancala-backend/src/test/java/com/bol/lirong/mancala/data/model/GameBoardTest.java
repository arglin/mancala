package com.bol.lirong.mancala.data.model;

import com.bol.lirong.mancala.data.model.GameBoard;
import com.bol.lirong.mancala.settings.MancalaDefault;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */
@SpringBootTest()
public class GameBoardTest {

    /**
     * initial GameBoard with MancalaDefault settings
     */
    @Test
    public void testNewDefaultGameBoard() {

        //given
        int[] pits = new int[MancalaDefault.PITS_NUMBER];
        Arrays.fill(pits, MancalaDefault.STONES_NUMBER_ON_SMALL_PIT);

        //when
        GameBoard gameBoard = new GameBoard();

        //then
        Assertions.assertEquals(MancalaDefault.PITS_NUMBER, gameBoard.getFirstPits().length);
        Assertions.assertEquals(MancalaDefault.PITS_NUMBER, gameBoard.getSecondPits().length);
        Assertions.assertArrayEquals(pits, gameBoard.getFirstPits());
        Assertions.assertArrayEquals(gameBoard.getFirstPits(), gameBoard.getSecondPits());
        Assertions.assertEquals(MancalaDefault.STONES_NUMBER_ON_BIG_PIT, gameBoard.getFirstBigPit());
        Assertions.assertEquals(MancalaDefault.STONES_NUMBER_ON_BIG_PIT, gameBoard.getSecondBigPit());
    }
}
