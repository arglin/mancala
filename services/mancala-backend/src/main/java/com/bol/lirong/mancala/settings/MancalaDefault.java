package com.bol.lirong.mancala.settings;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */

public final class MancalaDefault {

    /**
     * initial number of small pits for each player
     */
    public static final int PITS_NUMBER = 6;

    /**
     * initial number of stones for each small pit
     */
    public static final int STONES_NUMBER_ON_SMALL_PIT = 6;

    /**
     * initial number of stones for each big pit
     */
    public static final int STONES_NUMBER_ON_BIG_PIT = 0;

    /**
     * websocket endpoint
     */
    public static final String WEBSOCKET_ENDPOINT = "/game";

    /**
     * Game info subscription prefix
     */
    public static final String GAME_INFO_SUB = "/reply/gameInfo";

    /**
     * the prefix of application destination
     */
    public static final String APP_DEST_PREFIX = "/mancala";

    /**
     * the prefix of user destination
     */
    public static final String USER_DEST_PREFIX = "/private";

}
