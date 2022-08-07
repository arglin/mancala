package com.bol.lirong.mancala.data.response;

import com.bol.lirong.mancala.data.model.Player;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@SpringBootTest()
public class PlayerResponseTest {

    @Test
    public void testConstructWithPlayer() {

        //given
        Player player = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player.setGameId(UUID.randomUUID().toString());
        player.setTotalMatch(34);
        player.setWinMatch(20);
        player.setTieMatch(3);

        //when
        PlayerResponse playerResponse = new PlayerResponse(player);

        //then
        Assertions.assertEquals(player.getPlayerId(), playerResponse.getPlayerId());
        Assertions.assertEquals(player.getPlayerName(), playerResponse.getPlayerName());
        Assertions.assertEquals(player.getTotalMatch(), playerResponse.getTotalMatch());
        Assertions.assertEquals(player.getWinMatch(), playerResponse.getWinMatch());
        Assertions.assertEquals(player.getTieMatch(), playerResponse.getTieMatch());
        Assertions.assertNull(playerResponse.getErrorMsg());
    }
}
