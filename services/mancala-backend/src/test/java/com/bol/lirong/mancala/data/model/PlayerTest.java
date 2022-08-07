package com.bol.lirong.mancala.data.model;

import com.bol.lirong.mancala.data.model.Player;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest()
public class PlayerTest {

    private Player player;

    @Mock
    private WebSocketSession webSocketSession;

    @BeforeEach
    public void setUp() {
        Faker faker = new Faker();
        this.player = new Player(webSocketSession.getId(), faker.funnyName().name());
    }
    /**
     * for the default constructor of Player,
     * gameId should not be initialed
     */
    @Test
    public void testNewDefaultPlayer() {

        Assertions.assertEquals(this.webSocketSession.getId(), this.player.getPlayerId());
        Assertions.assertNotNull(this.player.getPlayerName());
        Assertions.assertNull(this.player.getGameId());
        Assertions.assertEquals(0, this.player.getTotalMatch());
        Assertions.assertEquals(0, this.player.getWinMatch());
        Assertions.assertEquals(0, this.player.getTieMatch());
    }
}
