package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.GameStatus;

/**
 * @author linlirong
 * @created 27/02/2022
 * @project mancala
 */
public enum GameStatusResponse {

    /**
     * waiting for opponent
     */
    WAITING_FOR_OPPONENT,

    /**
     * my turn
     */
    MY_TURN,

    /**
     * opponent's turn
     */
    OPPONENT_TURN,

    /**
     * I win
     */
    WIN,

    /**
     * opponent win
     */
    LOSE,

    /**
     * game is a tie
     */
    TIE,

    /**
     * I quit
     */
    I_QUIT,

    /**
     * opponent quit
     */
    OPPONENT_QUIT,

    /**
     * game is canceled
     */
    CANCEL;

    /**
     * translate from GameStatus to GameStatusResponse
     *
     * @param gameStatus       GameStatus
     * @param isForFirstPlayer is it for the first player
     * @return GameStatusResponse
     */
    public static GameStatusResponse toGameStatusResponse(GameStatus gameStatus, boolean isForFirstPlayer) {

        // translate the status to player's view
        switch (gameStatus) {

            case PENDING -> {
                return WAITING_FOR_OPPONENT;
            }
            case ONGOING_P1 -> {
                return isForFirstPlayer ? MY_TURN : OPPONENT_TURN;
            }
            case ONGOING_P2 -> {
                return isForFirstPlayer ? OPPONENT_TURN : MY_TURN;
            }
            case FINISHED_WON_P1 -> {
                return isForFirstPlayer ? WIN : LOSE;
            }
            case FINISHED_WON_P2 -> {
                return isForFirstPlayer ? LOSE : WIN;
            }
            case FINISHED_TIE -> {
                return TIE;
            }
            case FINISHED_QUIT_P1 -> {
                return isForFirstPlayer ? I_QUIT : OPPONENT_QUIT;
            }
            case FINISHED_QUIT_P2 -> {
                return isForFirstPlayer ? OPPONENT_QUIT : I_QUIT;
            }
            case FINISHED_CANCEL -> {
                return CANCEL;
            }
            default -> {
                return null;
            }
        }
    }
}
