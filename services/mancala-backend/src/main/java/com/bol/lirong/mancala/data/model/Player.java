package com.bol.lirong.mancala.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */

@Data
@Document("players")
public class Player {

    /**
     * unique id of the player
     */
    @Id
    @Indexed(unique=true)
    private String playerId;

    /**
     * name of the player in the game
     */
    private String playerName;

    /**
     * the gameId of the game that the player is attending
     * set when player join the game
     */
    private String gameId;

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

    public Player(String playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }
}
