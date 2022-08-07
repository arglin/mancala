package com.bol.lirong.mancala.service;

import com.bol.lirong.mancala.exception.InvalidGameInputException;
import com.bol.lirong.mancala.exception.InvalidGameStateException;
import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameStatus;
import com.bol.lirong.mancala.data.model.Player;
import com.bol.lirong.mancala.data.repository.GameRepository;
import com.bol.lirong.mancala.data.repository.PlayerRepository;
import com.bol.lirong.mancala.settings.MancalaDefault;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author linlirong
 * @created 25/02/2022
 * @project mancala
 */
@SpringBootTest()
public class MancalaGameServiceTest {

    @Autowired
    private MancalaGameService mancalaGameService;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @AfterEach
    public void tearDown() {
        this.playerRepository.deleteAll();
        this.gameRepository.deleteAll();
    }

    @Test
    public void testNewPlayerConnected() {

        //given
        String playerId = UUID.randomUUID().toString();

        //when
        this.mancalaGameService.playerConnected(playerId);
        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);

        //then
        Assertions.assertTrue(playerOptional.isPresent());
        Assertions.assertEquals(playerId, playerOptional.get().getPlayerId());
        Assertions.assertEquals(playerId, playerOptional.get().getPlayerId());
        Assertions.assertNotNull(playerOptional.get().getPlayerName());
        //not join the game yet, so is null
        Assertions.assertNull(playerOptional.get().getGameId());
        Assertions.assertEquals(0, playerOptional.get().getTotalMatch());
        Assertions.assertEquals(0, playerOptional.get().getWinMatch());
        Assertions.assertEquals(0, playerOptional.get().getTieMatch());
    }

    @Test
    public void testOldPlayerConnectedNoCreate() {

        //given
        String playerId = UUID.randomUUID().toString();
        Player player = this.mancalaGameService.playerConnected(playerId);

        //when player connect again
        this.mancalaGameService.playerConnected(playerId);
        List<Player> players = this.playerRepository.findAll();

        //then
        Assertions.assertEquals(1, players.size());
        Assertions.assertEquals(player, players.get(0));
    }

    /**
     * when player is not created yet, but request to start the game
     * will create the player and the game
     */
    @Test
    public void testCreateNewGameWhenPlayerNotCreatedStartPlay() {

        //given
        String playerId = UUID.randomUUID().toString();

        //when
        this.mancalaGameService.playerJoin(playerId);
        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        Assertions.assertTrue(playerOptional.isPresent());

        Optional<Game> gameOptional = this.gameRepository.findGameByGameId(playerOptional.get().getGameId());

        //then
        Assertions.assertTrue(gameOptional.isPresent());
        Assertions.assertEquals(gameOptional.get().getFirstPlayer(), playerOptional.get());
        Assertions.assertEquals(GameStatus.PENDING, gameOptional.get().getGameStatus());
        Assertions.assertNull(gameOptional.get().getSecondPlayer());
    }

    /**
     * when player is created already, and request to start the game
     * will create the player and the game
     */
    @Test
    public void testCreateNewGameWhenPlayerCreatedStartPlay() {

        //given
        String playerId = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId);

        //when
        this.mancalaGameService.playerJoin(playerId);
        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        Assertions.assertTrue(playerOptional.isPresent());

        Optional<Game> gameOptional = this.gameRepository.findGameByGameId(playerOptional.get().getGameId());

        //then
        Assertions.assertTrue(gameOptional.isPresent());
        Assertions.assertEquals(gameOptional.get().getFirstPlayer(), playerOptional.get());
        Assertions.assertEquals(GameStatus.PENDING, gameOptional.get().getGameStatus());
        Assertions.assertNull(gameOptional.get().getSecondPlayer());
    }

    @Test
    public void testDoNothingWhenTryJoinTheGameAgainWhileGameOngoing() {

        //given
        String playerId1 = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId1);
        String playerId2 = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId2);
        this.mancalaGameService.playerJoin(playerId1);
        this.mancalaGameService.playerJoin(playerId2);

        //when
        this.mancalaGameService.playerJoin(playerId1);
        Optional<Player> playerOptional1 = this.playerRepository.findPlayerByPlayerId(playerId1);
        Optional<Player> playerOptional2 = this.playerRepository.findPlayerByPlayerId(playerId2);
        Assertions.assertTrue(playerOptional1.isPresent());
        Optional<Game> gameOptional = this.gameRepository.findGameByGameId(playerOptional1.get().getGameId());

        //then
        Assertions.assertTrue(playerOptional2.isPresent());
        Assertions.assertTrue(gameOptional.isPresent());
        Assertions.assertEquals(gameOptional.get().getFirstPlayer(), playerOptional1.get());
        Assertions.assertEquals(gameOptional.get().getSecondPlayer(), playerOptional2.get());
        Assertions.assertEquals(GameStatus.ONGOING_P1, gameOptional.get().getGameStatus());
    }

    @Test
    public void testTheGameWithMultiPendingGamesAndJoinTheEarliestGameAsSecondPlayer() {

        //given
        String playerId1 = UUID.randomUUID().toString();
        Player player1 = this.mancalaGameService.playerConnected(playerId1);
        String playerId2 = UUID.randomUUID().toString();
        Player player2 = this.mancalaGameService.playerConnected(playerId2);
        String playerId3 = UUID.randomUUID().toString();
        Player player3 = this.mancalaGameService.playerConnected(playerId3);
        String playerId4 = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId4);

        Game game1 = new Game();
        game1.setCreateTime(LocalDateTime.now());
        game1.setFirstPlayer(player1);

        Game game2 = new Game();
        game2.setCreateTime(LocalDateTime.now().plusMinutes(30));
        game2.setFirstPlayer(player2);

        Game game3 = new Game();
        game3.setCreateTime(LocalDateTime.now().plusMinutes(40));
        game3.setFirstPlayer(player3);
        this.gameRepository.save(game1);
        this.gameRepository.save(game2);
        this.gameRepository.save(game3);

        //when
        Game joinedGame = this.mancalaGameService.playerJoin(playerId4);
        Optional<Player> player4_updated = this.playerRepository.findPlayerByPlayerId(playerId4);

        Assertions.assertTrue(player4_updated.isPresent());
        Assertions.assertEquals(game1.getGameId(), joinedGame.getGameId());
        Assertions.assertEquals(joinedGame.getFirstPlayer(), player1);
        Assertions.assertEquals(joinedGame.getSecondPlayer(), player4_updated.get());
        Assertions.assertEquals(GameStatus.ONGOING_P1, joinedGame.getGameStatus());
    }

    @Test
    public void testSowNoSuchPlayerException() {

        Assertions.assertThrows(InvalidGameInputException.class, () -> this.mancalaGameService.playerSow(UUID.randomUUID().toString(), 3));
    }

    @Test
    public void testSowGameNotExistsException() {

        //given
        Player player = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());

        Assertions.assertThrows(InvalidGameInputException.class, () -> this.mancalaGameService.playerSow(player.getPlayerId(), 3));
    }

    @Test
    public void testSowGameIsPendingException() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());

        //then
        Assertions.assertThrows(InvalidGameStateException.class, () -> this.mancalaGameService.playerSow(player1.getPlayerId(), 3));
    }

    @Test
    public void testSowGameAlreadyFinishedException() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        Game game = this.mancalaGameService.playerJoin(player2.getPlayerId());
        //set game finished
        game.setGameStatus(GameStatus.FINISHED_WON_P2);
        this.gameRepository.save(game);

        Assertions.assertThrows(InvalidGameStateException.class, () -> this.mancalaGameService.playerSow(player1.getPlayerId(), 3));
    }

    @Test
    public void testSowNotSecondPlayerTurnException() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        //initial gamestatus is ONGOING_P1, first player's turn
        this.mancalaGameService.playerJoin(player2.getPlayerId());

        Assertions.assertThrows(InvalidGameStateException.class, () -> this.mancalaGameService.playerSow(player2.getPlayerId(), 3));
    }

    @Test
    public void testSowNotFirstPlayerTurnException() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        //initial game status is ONGOING_P1, first player's turn
        this.mancalaGameService.playerJoin(player2.getPlayerId());
        this.mancalaGameService.playerSow(player1.getPlayerId(), 2);

        // now is second player's turn
        Assertions.assertThrows(InvalidGameStateException.class, () -> this.mancalaGameService.playerSow(player1.getPlayerId(), 5));
    }

    @Test
    public void testSowInvalidPitIndexException() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        //initial game status is ONGOING_P1, first player's turn
        this.mancalaGameService.playerJoin(player2.getPlayerId());

        // now is second player's turn, sow a invalid pit number
        Assertions.assertThrows(InvalidGameInputException.class, () -> this.mancalaGameService.playerSow(player1.getPlayerId(), MancalaDefault.PITS_NUMBER));
        Assertions.assertThrows(InvalidGameInputException.class, () -> this.mancalaGameService.playerSow(player1.getPlayerId(), -1));
    }

    @Test
    public void testSowOnEmptyPitException() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        //initial game status is ONGOING_P1, first player's turn
        this.mancalaGameService.playerJoin(player2.getPlayerId());

        this.mancalaGameService.playerSow(player1.getPlayerId(), 5);
        this.mancalaGameService.playerSow(player2.getPlayerId(), 3);
        // after pit 2 is empty
        Assertions.assertThrows(InvalidGameInputException.class, () -> this.mancalaGameService.playerSow(player1.getPlayerId(), 5));

        this.mancalaGameService.playerSow(player1.getPlayerId(), 0);
        //secondPits index 3 is 0
        Assertions.assertThrows(InvalidGameInputException.class, () -> this.mancalaGameService.playerSow(player2.getPlayerId(), 3));
    }

    @Test
    public void testSowPitSuccess() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        this.mancalaGameService.playerJoin(player2.getPlayerId());

        //first player sow
        Game game = this.mancalaGameService.playerSow(player1.getPlayerId(), 3);
        Assertions.assertArrayEquals(new int[]{6, 6, 6, 0, 7, 7}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{7, 7, 7, 6, 6, 6}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(1, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(0, game.getGameBoard().getSecondBigPit());

        //second player sow
        game = this.mancalaGameService.playerSow(player2.getPlayerId(), 0);
        Assertions.assertArrayEquals(new int[]{7, 6, 6, 0, 7, 7}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{0, 8, 8, 7, 7, 7}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(1, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(1, game.getGameBoard().getSecondBigPit());
    }

    @Test
    public void testSowFirstPlayerWin() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        Game game = this.mancalaGameService.playerJoin(player2.getPlayerId());

        //when
        game.getGameBoard().setFirstPits(new int[]{0, 0, 0, 0, 0, 3});
        game.getGameBoard().setSecondPits(new int[]{1, 4, 0, 1, 9, 3});
        game.getGameBoard().setFirstBigPit(43);
        game.getGameBoard().setSecondBigPit(8);
        game.setGameStatus(GameStatus.ONGOING_P1);
        this.gameRepository.save(game);

        game = this.mancalaGameService.playerSow(player1.getPlayerId(), 5);
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(44, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(28, game.getGameBoard().getSecondBigPit());
        Assertions.assertEquals(GameStatus.FINISHED_WON_P1, game.getGameStatus());
    }

    @Test
    public void testSowSecondPlayerWin() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        Game game = this.mancalaGameService.playerJoin(player2.getPlayerId());

        //when
        game.getGameBoard().setFirstPits(new int[]{3, 2, 0, 4, 6, 4});
        game.getGameBoard().setSecondPits(new int[]{0, 0, 0, 0, 0, 2});
        game.getGameBoard().setFirstBigPit(8);
        game.getGameBoard().setSecondBigPit(43);
        game.setGameStatus(GameStatus.ONGOING_P2);
        this.gameRepository.save(game);

        game = this.mancalaGameService.playerSow(player2.getPlayerId(), 5);
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(44, game.getGameBoard().getSecondBigPit());
        Assertions.assertEquals(28, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(GameStatus.FINISHED_WON_P2, game.getGameStatus());
    }

    @Test
    public void testSowGameIsATie() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        Game game = this.mancalaGameService.playerJoin(player2.getPlayerId());

        //when
        game.getGameBoard().setFirstPits(new int[]{3, 2, 0, 4, 6, 3});
        game.getGameBoard().setSecondPits(new int[]{0, 0, 0, 0, 0, 1});
        game.getGameBoard().setFirstBigPit(18);
        game.getGameBoard().setSecondBigPit(35);
        game.setGameStatus(GameStatus.ONGOING_P2);
        this.gameRepository.save(game);

        game = this.mancalaGameService.playerSow(player2.getPlayerId(), 5);
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 0}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(36, game.getGameBoard().getSecondBigPit());
        Assertions.assertEquals(36, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(GameStatus.FINISHED_TIE, game.getGameStatus());
    }

    @Test
    public void testSowCaptureStone() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        Game game = this.mancalaGameService.playerJoin(player2.getPlayerId());

        //when
        game.getGameBoard().setFirstPits(new int[]{3, 2, 0, 4, 6, 3});
        game.getGameBoard().setSecondPits(new int[]{0, 1, 0, 0, 0, 10});
        game.getGameBoard().setFirstBigPit(18);
        game.getGameBoard().setSecondBigPit(35);
        game.setGameStatus(GameStatus.ONGOING_P2);
        this.gameRepository.save(game);

        game = this.mancalaGameService.playerSow(player2.getPlayerId(), 1);
        Assertions.assertArrayEquals(new int[]{3, 2, 0, 0, 6, 3}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{0, 0, 0, 0, 0, 10}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(18, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(40, game.getGameBoard().getSecondBigPit());
        Assertions.assertEquals(GameStatus.ONGOING_P1, game.getGameStatus());
    }

    @Test
    public void testSowSecondPlayerOneMoreTurn() {

        //given
        Player player1 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        Player player2 = this.mancalaGameService.playerConnected(UUID.randomUUID().toString());
        this.mancalaGameService.playerJoin(player1.getPlayerId());
        Game game = this.mancalaGameService.playerJoin(player2.getPlayerId());

        //when
        game.getGameBoard().setFirstPits(new int[]{3, 2, 0, 4, 6, 3});
        game.getGameBoard().setSecondPits(new int[]{0, 1, 0, 0, 2, 0});
        game.getGameBoard().setFirstBigPit(18);
        game.getGameBoard().setSecondBigPit(43);
        game.setGameStatus(GameStatus.ONGOING_P2);
        this.gameRepository.save(game);

        game = this.mancalaGameService.playerSow(player2.getPlayerId(), 4);
        Assertions.assertArrayEquals(new int[]{3, 2, 0, 4, 6, 3}, game.getGameBoard().getFirstPits());
        Assertions.assertArrayEquals(new int[]{0, 1, 0, 0, 0, 1}, game.getGameBoard().getSecondPits());
        Assertions.assertEquals(18, game.getGameBoard().getFirstBigPit());
        Assertions.assertEquals(44, game.getGameBoard().getSecondBigPit());
        // last stone is on big pit, so have another chance to row
        Assertions.assertEquals(GameStatus.ONGOING_P2, game.getGameStatus());
    }

    @Test
    public void testPlayerDisconnectedNoSuchPlayer() {

        //given
        String playerId = UUID.randomUUID().toString();
        this.mancalaGameService.playerDisConnected(playerId);

        //when
        Optional<Player> player = this.playerRepository.findPlayerByPlayerId(playerId);
        Assertions.assertTrue(player.isEmpty());
    }

    @Test
    public void testPlayerDisconnectedNoGameRelated() {

        //given
        String playerId = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId);
        this.mancalaGameService.playerDisConnected(playerId);

        //when
        Optional<Player> player = this.playerRepository.findPlayerByPlayerId(playerId);

        //then
        Assertions.assertTrue(player.isEmpty());
    }

    @Test
    public void testFirstPlayerDisconnectedWhenWaitingForSecondPlayer() {

        //given
        String playerId = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId);
        Game game = this.mancalaGameService.playerJoin(playerId);

        //when
        this.mancalaGameService.playerDisConnected(playerId);
        Optional<Player> player = this.playerRepository.findPlayerByPlayerId(playerId);
        Optional<Game> gameAfter = this.gameRepository.findGameByGameId(game.getGameId());

        Assertions.assertTrue(player.isEmpty());
        Assertions.assertTrue(gameAfter.isPresent());
        Assertions.assertEquals(GameStatus.FINISHED_CANCEL, gameAfter.get().getGameStatus());
    }

    @Test
    public void testFirstPlayerDisconnectedWhenPlayingWithSecondPlayer() {

        //given
        String playerId1 = UUID.randomUUID().toString();
        String playerId2 = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId1);
        this.mancalaGameService.playerConnected(playerId2);
        this.mancalaGameService.playerJoin(playerId1);
        Game game = this.mancalaGameService.playerJoin(playerId2);

        //when
        this.mancalaGameService.playerDisConnected(playerId1);
        Optional<Player> playerOptional1 = this.playerRepository.findPlayerByPlayerId(playerId1);
        Optional<Player> playerOptional2 = this.playerRepository.findPlayerByPlayerId(playerId2);
        Optional<Game> gameAfter = this.gameRepository.findGameByGameId(game.getGameId());

        //then
        Assertions.assertTrue(playerOptional1.isEmpty());
        Assertions.assertTrue(playerOptional2.isPresent());
        Assertions.assertTrue(gameAfter.isPresent());
        Assertions.assertEquals(GameStatus.FINISHED_QUIT_P1, gameAfter.get().getGameStatus());
        Assertions.assertEquals(1, playerOptional2.get().getTotalMatch());
        Assertions.assertEquals(1, playerOptional2.get().getWinMatch());
        Assertions.assertEquals(0, playerOptional2.get().getTieMatch());
        Assertions.assertEquals(playerOptional2.get(), gameAfter.get().getSecondPlayer());
        Assertions.assertEquals(gameAfter.get().getGameId(), gameAfter.get().getFirstPlayer().getGameId());
        Assertions.assertEquals(1, gameAfter.get().getFirstPlayer().getTotalMatch());
        Assertions.assertEquals(0, gameAfter.get().getFirstPlayer().getWinMatch());
        Assertions.assertEquals(0, gameAfter.get().getFirstPlayer().getTieMatch());
    }

    @Test
    public void testSecondPlayerDisconnectedWhenPlayingWithFirstPlayer() {

        //given
        String playerId1 = UUID.randomUUID().toString();
        String playerId2 = UUID.randomUUID().toString();
        this.mancalaGameService.playerConnected(playerId1);
        this.mancalaGameService.playerConnected(playerId2);
        this.mancalaGameService.playerJoin(playerId1);
        Game game = this.mancalaGameService.playerJoin(playerId2);

        //when
        this.mancalaGameService.playerDisConnected(playerId2);
        Optional<Player> playerOptional1 = this.playerRepository.findPlayerByPlayerId(playerId1);
        Optional<Player> playerOptional2 = this.playerRepository.findPlayerByPlayerId(playerId2);
        Optional<Game> gameAfter = this.gameRepository.findGameByGameId(game.getGameId());

        //then
        Assertions.assertTrue(playerOptional1.isPresent());
        Assertions.assertTrue(playerOptional2.isEmpty());
        Assertions.assertTrue(gameAfter.isPresent());
        Assertions.assertEquals(GameStatus.FINISHED_QUIT_P2, gameAfter.get().getGameStatus());
        Assertions.assertEquals(1, playerOptional1.get().getTotalMatch());
        Assertions.assertEquals(1, playerOptional1.get().getWinMatch());
        Assertions.assertEquals(0, playerOptional1.get().getTieMatch());
        Assertions.assertEquals(playerOptional1.get(), gameAfter.get().getFirstPlayer());
        Assertions.assertEquals(gameAfter.get().getGameId(), gameAfter.get().getSecondPlayer().getGameId());
        Assertions.assertEquals(1, gameAfter.get().getSecondPlayer().getTotalMatch());
        Assertions.assertEquals(0, gameAfter.get().getSecondPlayer().getWinMatch());
        Assertions.assertEquals(0, gameAfter.get().getSecondPlayer().getTieMatch());
    }
}
