package com.bol.lirong.mancala.data.repository;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameStatus;
import com.bol.lirong.mancala.data.model.Player;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */

@ExtendWith(MockitoExtension.class)
@SpringBootTest()
public class GameRepositoryTest {

    @Autowired
    private GameRepository gameRepository;

    @AfterEach
    public void tearDown() {
        this.gameRepository.deleteAll();
    }

    @Test
    public void testFindGameByGameIdResultSuccess() {

        //given
        Faker faker = new Faker();
        Game game = new Game();
        Player firstPlayer = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        Player secondPlayer = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.now().plusMinutes(20);
        game.setFirstPlayer(firstPlayer);
        game.setSecondPlayer(secondPlayer);
        game.setCreateTime(createTime);
        game.setStartTime(startTime);
        this.gameRepository.save(game);

        //when
        Optional<Game> gameDB = this.gameRepository.findGameByGameId(game.getGameId());

        //then
        Assertions.assertTrue(gameDB.isPresent());
        Assertions.assertEquals(game.getGameId(), gameDB.get().getGameId());
        Assertions.assertEquals(game.getGameBoard(), gameDB.get().getGameBoard());
        Assertions.assertEquals(game.getGameStatus(), gameDB.get().getGameStatus());
        Assertions.assertEquals(game.getFirstPlayer(), gameDB.get().getFirstPlayer());
        Assertions.assertEquals(game.getSecondPlayer(), gameDB.get().getSecondPlayer());
        Assertions.assertEquals(game.getCreateTime().format(DateTimeFormatter.BASIC_ISO_DATE), gameDB.get().getCreateTime().format(DateTimeFormatter.BASIC_ISO_DATE));
        Assertions.assertEquals(game.getStartTime().format(DateTimeFormatter.BASIC_ISO_DATE), gameDB.get().getStartTime().format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    @Test
    public void testFindGameByGameIdResultEmpty() {

        //given no data
        //when
        Optional<Game> gameDB = this.gameRepository.findGameByGameId(UUID.randomUUID().toString());

        //then
        Assertions.assertTrue(gameDB.isEmpty());
    }

    @Test
    public void testFindGamesByPendingGameStatusResultSuccess() {

        //given three games in db, pendingGame1, pendingGame2, ongoingP1Game
        Faker faker = new Faker();
        Game pendingGame1 = new Game();
        Player firstPlayer1 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime1 = LocalDateTime.now();
        pendingGame1.setGameStatus(GameStatus.PENDING);
        pendingGame1.setFirstPlayer(firstPlayer1);
        pendingGame1.setCreateTime(createTime1);

        Game pendingGame2 = new Game();
        Player firstPlayer2 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime2 = LocalDateTime.now();
        pendingGame2.setGameStatus(GameStatus.PENDING);
        pendingGame2.setFirstPlayer(firstPlayer2);
        pendingGame2.setCreateTime(createTime2);

        Game ongoingP1Game3 = new Game();
        Player firstPlayer3 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        Player secondPlayer3 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime3 = LocalDateTime.now();
        LocalDateTime startTime3 = LocalDateTime.now().plusMinutes(10);
        ongoingP1Game3.setFirstPlayer(firstPlayer3);
        ongoingP1Game3.setSecondPlayer(secondPlayer3);
        ongoingP1Game3.setGameStatus(GameStatus.ONGOING_P1);
        ongoingP1Game3.setCreateTime(createTime3);
        ongoingP1Game3.setStartTime(startTime3);

        this.gameRepository.save(pendingGame1);
        this.gameRepository.save(pendingGame2);
        this.gameRepository.save(ongoingP1Game3);

        //when find PENDING games
        Optional<List<Game>> pendingGamesDB = this.gameRepository.findGamesByGameStatus(GameStatus.PENDING);

        //then
        Assertions.assertTrue(pendingGamesDB.isPresent());
        Assertions.assertEquals(2, pendingGamesDB.get().size());
        Assertions.assertEquals(pendingGamesDB.get().get(0).getGameStatus(), GameStatus.PENDING);
        Assertions.assertEquals(pendingGamesDB.get().get(1).getGameStatus(), GameStatus.PENDING);
    }

    @Test
    public void testFindGamesByOngoing_P2GameStatusResultEmpty() {

        //given three games in db: pendingGame1, pendingGame2, ongoingP1Game
        Faker faker = new Faker();
        Game pendingGame1 = new Game();

        Player firstPlayer1 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime1 = LocalDateTime.now().plusMinutes(9);
        pendingGame1.setGameStatus(GameStatus.PENDING);
        pendingGame1.setFirstPlayer(firstPlayer1);
        pendingGame1.setCreateTime(createTime1);

        Game pendingGame2 = new Game();
        Player firstPlayer2 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime2 = LocalDateTime.now();
        pendingGame2.setGameStatus(GameStatus.PENDING);
        pendingGame2.setFirstPlayer(firstPlayer2);
        pendingGame2.setCreateTime(createTime2);

        Game ongoingP1Game3 = new Game();
        Player firstPlayer3 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        Player secondPlayer3 = new Player(UUID.randomUUID().toString(), faker.funnyName().name());
        LocalDateTime createTime3 = LocalDateTime.now();
        LocalDateTime startTime3 = LocalDateTime.now().plusMinutes(30);
        ongoingP1Game3.setFirstPlayer(firstPlayer3);
        ongoingP1Game3.setSecondPlayer(secondPlayer3);
        ongoingP1Game3.setGameStatus(GameStatus.ONGOING_P1);
        ongoingP1Game3.setCreateTime(createTime3);
        ongoingP1Game3.setStartTime(startTime3);

        this.gameRepository.save(pendingGame1);
        this.gameRepository.save(pendingGame2);
        this.gameRepository.save(ongoingP1Game3);

        //when find ONGOING_P2 game
        Optional<List<Game>> OngoingP2GamesDB = this.gameRepository.findGamesByGameStatus(GameStatus.ONGOING_P2);

        //then
        Assertions.assertTrue(OngoingP2GamesDB.isPresent());
        Assertions.assertEquals(0, OngoingP2GamesDB.get().size());
    }
}
