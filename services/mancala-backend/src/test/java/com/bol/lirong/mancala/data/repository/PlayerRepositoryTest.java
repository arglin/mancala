package com.bol.lirong.mancala.data.repository;

import com.bol.lirong.mancala.data.model.Player;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */

@SpringBootTest()
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @AfterEach
    public void tearDown() {
        this.playerRepository.deleteAll();
    }

    @Test
    public void testFindPlayerByPlayerIdResultSuccess() {

        //given
        Player player = new Player(UUID.randomUUID().toString(), Faker.instance().funnyName().name());
        player.setGameId(UUID.randomUUID().toString());
        player.setTotalMatch(140);
        player.setWinMatch(100);
        player.setTieMatch(15);
        this.playerRepository.save(player);

        //when
        Optional<Player> playerDB = this.playerRepository.findPlayerByPlayerId(player.getPlayerId());

        //then
        Assertions.assertTrue(playerDB.isPresent());
        Assertions.assertEquals(player.getPlayerId(), playerDB.get().getPlayerId());
        Assertions.assertEquals(player.getPlayerName(), playerDB.get().getPlayerName());
        Assertions.assertEquals(player.getGameId(), playerDB.get().getGameId());
        Assertions.assertEquals(player.getTotalMatch(), playerDB.get().getTotalMatch());
        Assertions.assertEquals(player.getWinMatch(), playerDB.get().getWinMatch());
        Assertions.assertEquals(player.getTieMatch(), playerDB.get().getTieMatch());
    }

    @Test
    public void testFindPlayerByPlayerIdResultEmpty() {

        //given no data
        //when
        Optional<Player> playerDB = this.playerRepository.findPlayerByPlayerId(UUID.randomUUID().toString());

        //then
        Assertions.assertTrue(playerDB.isEmpty());
    }

    @Test
    public void testDeletePlayerByPlayerIdResultSuccess() {

        //given
        Player player = new Player(UUID.randomUUID().toString(), Faker.instance().funnyName().name());
        player.setGameId(UUID.randomUUID().toString());
        player.setTotalMatch(100);
        player.setWinMatch(10);
        player.setTieMatch(85);
        this.playerRepository.save(player);

        //when
        this.playerRepository.deletePlayerByPlayerId(player.getPlayerId());
        Optional<Player> playerDB = this.playerRepository.findPlayerByPlayerId(player.getPlayerId());

        //then
        Assertions.assertTrue(playerDB.isEmpty());
    }
}
