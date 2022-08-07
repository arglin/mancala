package com.bol.lirong.mancala.data.response;

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
public class PlayerResponse {

    /**
     * unique id of the player
     */
    private String playerId;

    /**
     * name of the player in the game
     */
    private String playerName;

    /**
     * number of total matches that player played
     */
    private int totalMatch;

    /**
     * number of win matches that player played
     */
    private int winMatch;

    /**
     * number of tie matches that player played
     */
    private int tieMatch;

    /**
     * error message
     */
    private String errorMsg;

    public PlayerResponse(Player player) {
        this.playerId = player.getPlayerId();
        this.playerName = player.getPlayerName();
        this.totalMatch = player.getTotalMatch();
        this.winMatch = player.getWinMatch();
        this.tieMatch = player.getTieMatch();
        this.errorMsg = null;
    }
}
