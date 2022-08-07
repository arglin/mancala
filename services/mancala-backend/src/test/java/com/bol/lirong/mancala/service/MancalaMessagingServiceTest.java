package com.bol.lirong.mancala.service;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameStatus;
import com.bol.lirong.mancala.data.model.Player;
import com.bol.lirong.mancala.data.repository.GameRepository;
import com.bol.lirong.mancala.data.repository.PlayerRepository;
import com.bol.lirong.mancala.data.response.GameResponse;
import com.bol.lirong.mancala.settings.MancalaDefault;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * @author linlirong
 * @created 28/02/2022
 * @project mancala
 */
@Slf4j
@SpringBootTest()
@ExtendWith(MockitoExtension.class)
public class MancalaMessagingServiceTest {

    private MancalaMessagingService messagingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        this.messagingService = new MancalaMessagingServiceImpl(messagingTemplate, gameRepository, playerRepository);
    }

    @Test
    public void testSyncGameInfoToOnlyFirstPlayerWhenSecondPlayerQuit() {

        // given, secondPlayer not provide
        Game game = new Game();
        Player firstPlayer = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        Player secondPlayer = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        firstPlayer.setGameId(game.getGameId());
        game.setFirstPlayer(firstPlayer);
        game.setSecondPlayer(secondPlayer);
        game.setGameStatus(GameStatus.PENDING);
        GameResponse expectedResponsePlayer1 = new GameResponse(game, true);
        GameResponse expectedResponsePlayer2 = new GameResponse(game, true);

        given(this.playerRepository.findPlayerByPlayerId(firstPlayer.getPlayerId())).willReturn(Optional.of(firstPlayer));
        given(this.playerRepository.findPlayerByPlayerId(secondPlayer.getPlayerId())).willReturn(Optional.empty());

        // when
        this.messagingService.syncGameInfo(game);

        //then
        verify(messagingTemplate).convertAndSendToUser(firstPlayer.getPlayerId(), MancalaDefault.GAME_INFO_SUB, expectedResponsePlayer1);
        verify(messagingTemplate, never()).convertAndSendToUser(secondPlayer.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer2);
    }

    @Test
    public void testSyncGameInfoToOnlySecondPlayerWhenFirstPlayerQuit() {

        // given, firstPlayer not provide
        Game game = new Game();
        Player firstPlayer = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        Player secondPlayer = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        secondPlayer.setGameId(game.getGameId());
        game.setFirstPlayer(firstPlayer);
        game.setSecondPlayer(secondPlayer);
        game.setGameStatus(GameStatus.FINISHED_QUIT_P1);
        GameResponse expectedResponsePlayer1 = new GameResponse(game, false);
        GameResponse expectedResponsePlayer2 = new GameResponse(game, false);

        given(this.playerRepository.findPlayerByPlayerId(firstPlayer.getPlayerId())).willReturn(Optional.empty());
        given(this.playerRepository.findPlayerByPlayerId(secondPlayer.getPlayerId())).willReturn(Optional.of(secondPlayer));

        // when
        this.messagingService.syncGameInfo(game);

        //then
        verify(messagingTemplate, never()).convertAndSendToUser(firstPlayer.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer1);
        verify(messagingTemplate).convertAndSendToUser(secondPlayer.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer2);
    }

    @Test
    void testSyncGameInfoToBothPlayers() {

        //given
        Game game = new Game();
        Player player1 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        Player player2 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player1.setGameId(game.getGameId());
        player2.setGameId(game.getGameId());
        game.setFirstPlayer(player1);
        game.setSecondPlayer(player2);
        game.setGameStatus(GameStatus.ONGOING_P2);

        given(this.playerRepository.findPlayerByPlayerId(player1.getPlayerId())).willReturn(Optional.of(player1));
        given(this.playerRepository.findPlayerByPlayerId(player2.getPlayerId())).willReturn(Optional.of(player2));

        GameResponse expectedResponsePlayer1 = new GameResponse(game, true);
        GameResponse expectedResponsePlayer2 = new GameResponse(game, false);

        // when
        this.messagingService.syncGameInfo(game);

        //then
        verify(messagingTemplate).convertAndSendToUser(player1.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer1);
        verify(messagingTemplate).convertAndSendToUser(player2.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer2);
    }


    @Test
    void testSyncGameInfoToFirstPlayersWhenSecondPlayerInAnotherGame() {

        //given
        Game game = new Game();
        Player player1 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        Player player2 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player1.setGameId(game.getGameId());
        player2.setGameId(UUID.randomUUID().toString()); //second player in another game
        game.setFirstPlayer(player1);
        game.setSecondPlayer(player2);
        game.setGameStatus(GameStatus.ONGOING_P2);

        given(this.playerRepository.findPlayerByPlayerId(player1.getPlayerId())).willReturn(Optional.of(player1));
        given(this.playerRepository.findPlayerByPlayerId(player2.getPlayerId())).willReturn(Optional.of(player2));

        GameResponse expectedResponsePlayer1 = new GameResponse(game, true);
        GameResponse expectedResponsePlayer2 = new GameResponse(game, false);

        // when
        this.messagingService.syncGameInfo(game);

        //then
        verify(messagingTemplate).convertAndSendToUser(player1.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer1);
        verify(messagingTemplate, never()).convertAndSendToUser(player2.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer2);
    }

    @Test
    void testSyncGameInfoToFirstPlayersWhenFirstPlayerInAnotherGame() {

        //given
        Game game = new Game();
        Player player1 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        Player player2 = new Player(UUID.randomUUID().toString(), Faker.instance().team().name());
        player1.setGameId(UUID.randomUUID().toString()); //first player in another game
        player2.setGameId(game.getGameId());
        game.setFirstPlayer(player1);
        game.setSecondPlayer(player2);
        game.setGameStatus(GameStatus.ONGOING_P2);

        given(this.playerRepository.findPlayerByPlayerId(player1.getPlayerId())).willReturn(Optional.of(player1));
        given(this.playerRepository.findPlayerByPlayerId(player2.getPlayerId())).willReturn(Optional.of(player2));

        GameResponse expectedResponsePlayer1 = new GameResponse(game, true);
        GameResponse expectedResponsePlayer2 = new GameResponse(game, false);

        // when
        this.messagingService.syncGameInfo(game);

        //then
        verify(messagingTemplate, never()).convertAndSendToUser(player1.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer1);
        verify(messagingTemplate).convertAndSendToUser(player2.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                expectedResponsePlayer2);
    }
}
