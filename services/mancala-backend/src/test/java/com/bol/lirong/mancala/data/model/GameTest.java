package com.bol.lirong.mancala.data.model;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */
@SpringBootTest()
public class GameTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        this.game = new Game();
    }

    /**
     * for the default constructor of Game,
     * only init gameId, gameBoard and gameStatus
     */
    @Test
    public void testNewDefaultGame() {

        Assertions.assertNotNull(game);
        Assertions.assertNotNull(game.getGameId());
        Assertions.assertNotNull(game.getGameBoard());
        Assertions.assertNull(game.getFirstPlayer());
        Assertions.assertNull(game.getSecondPlayer());
        Assertions.assertEquals(GameStatus.PENDING, game.getGameStatus());
        Assertions.assertNull(game.getCreateTime());
        Assertions.assertNull(game.getStartTime());
    }
}
