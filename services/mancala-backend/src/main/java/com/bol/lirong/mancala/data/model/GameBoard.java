package com.bol.lirong.mancala.data.model;

import com.bol.lirong.mancala.settings.MancalaDefault;
import lombok.Data;
import java.util.Arrays;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 *
 * The Board of the mancala game
 */
@Data
public class GameBoard {

    /**
     * pits of the first player, each value represents the number of the stones on each pit
     */
    private int[] firstPits;

    /**
     * the number of stones on the big pit of the first player
     */
    private int firstBigPit;

    /**
     * pits of the second player, each value represents the number of the stones on each pit
     */
    private int[] secondPits;

    /**
     * the number of stones on the big pit of the second player
     */
    private int secondBigPit;

    public GameBoard() {
        this.firstPits = new int[MancalaDefault.PITS_NUMBER];
        this.firstBigPit = MancalaDefault.STONES_NUMBER_ON_BIG_PIT;
        this.secondPits = new int[MancalaDefault.PITS_NUMBER];
        this.secondBigPit = MancalaDefault.STONES_NUMBER_ON_BIG_PIT;

        Arrays.fill(this.firstPits, MancalaDefault.STONES_NUMBER_ON_SMALL_PIT);
        Arrays.fill(this.secondPits, MancalaDefault.STONES_NUMBER_ON_SMALL_PIT);
    }
}
