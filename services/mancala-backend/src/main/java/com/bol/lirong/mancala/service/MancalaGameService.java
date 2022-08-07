package com.bol.lirong.mancala.service;

import com.bol.lirong.mancala.exception.InvalidGameInputException;
import com.bol.lirong.mancala.exception.InvalidGameStateException;
import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.Player;

/**
 * @author linlirong
 * @created 25/02/2022
 * @project mancala
 */
public interface MancalaGameService {

    /**
     * create new player with a playerId
     * and set a random playerName for the player
     *
     * @param playerId use principal name as playerId
     * @return Player that has been created
     */
    Player playerConnected(String playerId);

    /**
     * Player requests to join game,
     * create a new game or join an existing game
     *
     * @param playerId use principal name as playerId
     * @return Game that the player join
     */
    Game playerJoin(String playerId);

    /**
     * <p>Game Play</p>
     * The player who begins with the first move picks up all the stones in any of his own six pits,
     * and sows the stones on to the right, one in each of the following pits, including his own big pit.
     * No stones are put in the opponents' big pit. If the player's last stone lands in his own big pit,
     * he gets another turn. This can be repeated several times before it's the other player's turn.
     * <p></p>
     * <p>Capturing Stones</p>
     * During the game the pits are emptied on both sides. Always when the last stone lands in an own empty pit,
     * the player captures his own stone and all stones in the opposite pit (the other player’s pit)
     * and puts them in his own (big or little?) pit.
     * <p></p>
     * <p>The Game Ends</p>
     * The game is over as soon as one of the sides runs out of stones.
     * The player who still has stones in his pits keeps them and puts them in his big pit.
     * The winner of the game is the player who has the most stones in his big pit.
     *
     * @param playerId use principal name as playerId
     * @param pitIndex which pit to sow
     * @return Game after sow
     */
    Game playerSow(String playerId, int pitIndex) throws InvalidGameInputException, InvalidGameStateException;

    /**
     * when player disconnected, need to cancel game or make the player who quits lose the game
     *
     * @param playerId use principal name as playerId
     * @return Game after player disconnected
     */
    Game playerDisConnected(String playerId);
}
