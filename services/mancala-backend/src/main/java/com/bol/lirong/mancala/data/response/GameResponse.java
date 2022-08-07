package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameBoard;
import com.bol.lirong.mancala.data.model.Player;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
@Data
@NoArgsConstructor
public class GameResponse {

    /**
     * myself
     */
    private PlayerResponse me;

    /**
     * opponent player
     */
    private PlayerResponse opponent;

    /**
     * board of the game
     */
    private GameBoardResponse gameBoard;

    /**
     * status of the game life cycle
     */
    private GameStatusResponse gameStatus;

    public GameResponse(Game game, boolean forFirstPlayer) {

        this.me = forFirstPlayer ? this.toPlayerResponse(game.getFirstPlayer())
                : this.toPlayerResponse(game.getSecondPlayer());
        this.opponent = forFirstPlayer ? this.toPlayerResponse(game.getSecondPlayer())
                : this.toPlayerResponse(game.getFirstPlayer());
        this.gameBoard = this.toGameBorderResponse(game.getGameBoard(), forFirstPlayer);
        this.gameStatus = GameStatusResponse.toGameStatusResponse(game.getGameStatus(), forFirstPlayer);
    }

    /**
     * trans
     * @param player
     * @return
     */
    private PlayerResponse toPlayerResponse(Player player) {
        return player == null ? null : new PlayerResponse(player);
    }

    private GameBoardResponse toGameBorderResponse(GameBoard gameBoard, boolean forFirstPlayer) {
        return gameBoard == null ? null : new GameBoardResponse(gameBoard, forFirstPlayer);
    }

}
