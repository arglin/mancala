package com.bol.lirong.mancala.service;

import com.bol.lirong.mancala.data.model.Game;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
public interface MancalaMessagingService {

    /**
     * sync game info to first player and second player
     * @param game Mancala Game
     */
    void syncGameInfo(Game game);

    /**
     * sync game info with error message to Player
     * @param playerId playerId
     * @param error error message
     */
    void syncGameInfo(String playerId, String error);
}
