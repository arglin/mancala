package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.GameBoard;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linlirong
 * @created 27/02/2022
 * @project mancala
 */
@Data
@NoArgsConstructor
public class GameBoardResponse {

    /**
     * pits of mine, each value represents the number of the stones on each pit
     */
    private int[] myPits;

    /**
     * the number of stones on the big pit of mine
     */
    private int myBigPit;

    /**
     * pits of the opponent, each value represents the number of the stones on each pit
     */
    private int[] opponentPits;

    /**
     * the number of stones on the big pit of the opponent
     */
    private int opponentBigPit;

    public GameBoardResponse(GameBoard gameBoard, boolean forFirstPlayer) {

        this.setMyPits(forFirstPlayer ? gameBoard.getFirstPits() : gameBoard.getSecondPits());
        this.setMyBigPit(forFirstPlayer ? gameBoard.getFirstBigPit() : gameBoard.getSecondBigPit());
        this.setOpponentPits(forFirstPlayer ? gameBoard.getSecondPits() : gameBoard.getFirstPits());
        this.setOpponentBigPit(forFirstPlayer ? gameBoard.getSecondBigPit() : gameBoard.getFirstBigPit());
    }
}
