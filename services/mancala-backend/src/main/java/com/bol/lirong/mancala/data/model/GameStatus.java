package com.bol.lirong.mancala.data.model;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */

public enum GameStatus {

    /**
     * game is created but first player is waiting
     */
    PENDING,

    /**
     * game is ongoing, first player's turn
     */
    ONGOING_P1,

    /**
     * game is ongoing, second player's turn
     */
    ONGOING_P2,

    /**
     * game is over, first player won
     */
    FINISHED_WON_P1,

    /**
     * game is over, second player won
     */
    FINISHED_WON_P2,

    /**
     * game is over, game is a tie
     */
    FINISHED_TIE,

    /**
     * game is quit by first player
     */
    FINISHED_QUIT_P1,

    /**
     * game is quit by second player
     */
    FINISHED_QUIT_P2,

    /**
     * game is canceled
     */
    FINISHED_CANCEL
}
