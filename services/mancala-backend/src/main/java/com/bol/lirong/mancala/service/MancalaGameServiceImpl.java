package com.bol.lirong.mancala.service;

import com.bol.lirong.mancala.exception.InvalidGameInputException;
import com.bol.lirong.mancala.exception.InvalidGameStateException;
import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameBoard;
import com.bol.lirong.mancala.data.model.GameStatus;
import com.bol.lirong.mancala.data.model.Player;
import com.bol.lirong.mancala.data.repository.GameRepository;
import com.bol.lirong.mancala.data.repository.PlayerRepository;
import com.bol.lirong.mancala.settings.MancalaDefault;
import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author linlirong
 * @created 25/02/2022
 * @project mancala
 */
@Service
@AllArgsConstructor
@Slf4j
public class MancalaGameServiceImpl implements MancalaGameService {

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    /**
     * create new player with a playerId
     * and set a random playerName for the player
     *
     * @param playerId use principal name as playerId
     * @return Player that has been created
     */
    @Override
    public Player playerConnected(String playerId) {

        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        if (playerOptional.isPresent()) {
            log.info("player was already created, {}", playerOptional.get());
            return playerOptional.get();
        }

        Player player = new Player(playerId, Faker.instance().team().name());
        log.info("new player created, {}", player);
        return this.playerRepository.save(player);
    }

    /**
     * Player requests to join game,
     * create a new game or join an existing game
     *
     * @param playerId use principal name as playerId
     * @return Game that the player join
     */
    @Override
    public Game playerJoin(String playerId) {

        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        Player player = playerOptional.orElse(null);
        //create player if not exists
        if (player == null) {
            Player newPlayer = new Player(playerId, Faker.instance().team().name());
            log.info("playerId = {} is not found, created new player, {}", playerId, newPlayer);
            player = this.playerRepository.save(newPlayer);
        }

        String gameId = player.getGameId();
        Optional<Game> gameOptional = this.gameRepository.findGameByGameId(gameId);
        Game game = gameOptional.orElse(null);

        // player is already in a game, neither create new game nor join another game
        if (game != null && this.isOngoingGameStatus(game.getGameStatus())) {

            log.info("playerId = {} is already joined a game {}", playerId, game);
            return game;
        }

        // player's game is already finished or not in any game, try get all pending games
        Optional<List<Game>> pendingGamesOptional = this.gameRepository.findGamesByGameStatus(GameStatus.PENDING);
        // there is no games now are pending, then create a new game, player is as firstPlayer
        if (pendingGamesOptional.isEmpty() || pendingGamesOptional.get().size() == 0) {

            log.info("there are no games in pending status, try to create one. player = {}", player);
            return this.createNewGame(player);
        }

        // there are games are in pending status, join one, the player is as secondPlayer.
        // strategy: join the earliest one. Or can have other strategies.
        Game joinGame = pendingGamesOptional.stream().flatMap(Collection::stream)
                .min(Comparator.comparing(Game::getCreateTime)).orElse(null);
        assert joinGame != null;
        log.info("join an existing game as a second player. playerId = {}, joinGame = {}", player.getPlayerId(), joinGame);
        return this.joinExistingGame(player, joinGame);
    }

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
    @Override
    public Game playerSow(String playerId, int pitIndex) {

        log.info("sow = {}, playId = {}", pitIndex, playerId);
        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        if (playerOptional.isEmpty()) {
            throw new InvalidGameInputException("no player found by playerId = " + playerId);
        }

        Optional<Game> gameOptional = this.gameRepository.findGameByGameId(playerOptional.get().getGameId());
        if (gameOptional.isEmpty()) {
            throw new InvalidGameInputException("no game found by playerId = " + playerId);
        }

        Player player = playerOptional.get();
        Game game = gameOptional.get();

        //move the stones from pitIndex to other pits
        this.sowStones(player, game, pitIndex);

        // audit game, decide if game finish and who wins
        this.auditGame(game);

        playerRepository.save(game.getFirstPlayer());
        playerRepository.save(game.getSecondPlayer());
        return gameRepository.save(game);
    }

    /**
     * when player disconnected, need to cancel game or make the player who quits lose the game
     *
     * @param playerId use principal name as playerId
     * @return Game after player disconnected
     */
    @Override
    public Game playerDisConnected(String playerId) {

        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        if (playerOptional.isEmpty()) {
            log.info("cannot find player by playerId = {}", playerId);
            return null;
        }
        Player player = playerOptional.get();

        //delete player when disconnected
        this.playerRepository.delete(player);

        Optional<Game> gameOptional = this.gameRepository.findGameByGameId(playerOptional.get().getGameId());
        if (gameOptional.isEmpty()) {
            log.info("no related game by playerId = {}", playerId);
            return null;
        }
        Game game = gameOptional.get();

        // first player quit while waiting for second player
        if (player.equals(game.getFirstPlayer()) && GameStatus.PENDING.equals(game.getGameStatus())) {

            log.info("first player = {} quit while waiting for the second player join, cancel game = {}", player, game);
            game.setGameStatus(GameStatus.FINISHED_CANCEL);
            return this.gameRepository.save(game);
        }

        // first player quit while playing the game with second player
        if (player.equals(game.getFirstPlayer()) && this.isOngoingGameStatus(game.getGameStatus())) {

            log.info("first player = {} quit while playing the game with second player = {}, game = {}", player, game.getSecondPlayer(), game);
            Player firstPlayer = game.getFirstPlayer();
            Player secondPlayer = game.getSecondPlayer();

            secondPlayer.setTotalMatch(secondPlayer.getTotalMatch() + 1);
            secondPlayer.setWinMatch(secondPlayer.getWinMatch() + 1);
            firstPlayer.setTotalMatch(firstPlayer.getTotalMatch() + 1);
            game.setGameStatus(GameStatus.FINISHED_QUIT_P1);

            //not save first player because of disconnection
            this.playerRepository.save(secondPlayer);
            this.gameRepository.save(game);
            return game;
        }

        // second player quit while on going the game with first player
        if (player.equals(game.getSecondPlayer()) && this.isOngoingGameStatus(game.getGameStatus())) {

            Player firstPlayer = game.getFirstPlayer();
            Player secondPlayer = game.getSecondPlayer();
            log.info("second player quit while on going the game with first player = {}, second player = {}, game = {}",
                    firstPlayer, secondPlayer, game);

            firstPlayer.setTotalMatch(firstPlayer.getTotalMatch() + 1);
            firstPlayer.setWinMatch(firstPlayer.getWinMatch() + 1);
            secondPlayer.setTotalMatch(secondPlayer.getTotalMatch() + 1);
            game.setGameStatus(GameStatus.FINISHED_QUIT_P2);

            //not save second player because of disconnection
            this.playerRepository.save(firstPlayer);
            this.gameRepository.save(game);
            return game;
        }

        return game;
    }

    /**
     * create new game when firstPlayer joins
     *
     * @param firstPlayer firstPlayer in the game
     * @return Game that has been created
     */
    private Game createNewGame(Player firstPlayer) {

        Game game = new Game();
        firstPlayer.setGameId(game.getGameId());
        game.setFirstPlayer(firstPlayer);
        game.setCreateTime(LocalDateTime.now());

        this.playerRepository.save(firstPlayer);
        return this.gameRepository.save(game);
    }

    /**
     * secondPlayer join to the existing game,
     * game starts for two players are ready
     *
     * @param secondPlayer secondPlayer in the game
     * @param game         game that is on PENDING status
     * @return Game that the second player joins
     */
    private Game joinExistingGame(Player secondPlayer, Game game) {

        secondPlayer.setGameId(game.getGameId());
        game.setSecondPlayer(secondPlayer);
        //set game status to first player's turn
        game.setGameStatus(GameStatus.ONGOING_P1);
        //game starts now
        game.setStartTime(LocalDateTime.now());
        this.playerRepository.save(secondPlayer);
        return this.gameRepository.save(game);
    }

    /**
     * check if the sow is valid
     *
     * @param player   the player who do the sow
     * @param game     the game in play
     * @param pitIndex the pit that stones to be sowed from
     */
    private void checkSow(Player player, Game game, int pitIndex) throws InvalidGameStateException,
            InvalidGameInputException {

        if (GameStatus.PENDING.equals(game.getGameStatus())) {
            throw new InvalidGameStateException("Waiting for opponent, cannot sow");
        }

        if (isFinishedGameStatus(game.getGameStatus())) {
            throw new InvalidGameStateException("Game is already finished, cannot sow");
        }

        if (game.getFirstPlayer().equals(player) && !GameStatus.ONGOING_P1.equals(game.getGameStatus())) {
            throw new InvalidGameStateException("It is not your turn yet, cannot sow");
        }

        if (game.getSecondPlayer().equals(player) && !GameStatus.ONGOING_P2.equals(game.getGameStatus())) {
            throw new InvalidGameStateException("It is not your turn yet, cannot sow");
        }

        if (!game.getFirstPlayer().equals(player) && !game.getSecondPlayer().equals(player)) {
            throw new InvalidGameStateException("Player not belong to the game, cannot sow");
        }

        if (pitIndex < 0 || pitIndex > MancalaDefault.PITS_NUMBER - 1) {
            throw new InvalidGameInputException("Invalid sow at " + pitIndex);
        }

        if (game.getFirstPlayer().equals(player) && game.getGameBoard().getFirstPits()[pitIndex] <= 0) {
            throw new InvalidGameInputException("No stone on selected, please try again");
        }

        if (game.getSecondPlayer().equals(player) && game.getGameBoard().getSecondPits()[pitIndex] <= 0) {
            throw new InvalidGameInputException("No stone on selected, please try again");
        }
    }

    /**
     * sow the stones based on mancala game rules
     *
     * @param player   the player who do the sow
     * @param game     the game in play
     * @param pitIndex the pit that stones to be sowed from
     */
    private synchronized void sowStones(Player player, Game game, int pitIndex) {

        //check the sow is valid
        this.checkSow(player, game, pitIndex);

        GameBoard gameBoard = game.getGameBoard();
        boolean isFirstPlayer = player.equals(game.getFirstPlayer());
        //pits of the player who sows
        int[] playerPits = isFirstPlayer ? gameBoard.getFirstPits() : gameBoard.getSecondPits();
        //pits of the opponent of the player who sows
        int[] opponentPits = isFirstPlayer ? gameBoard.getSecondPits() : gameBoard.getFirstPits();
        //big pit of the player who sows
        int playerBigPit = isFirstPlayer ? gameBoard.getFirstBigPit() : gameBoard.getSecondBigPit();

        // already checked pitIndex is valid during checkSow(..)
        int stonesToSow = playerPits[pitIndex];

        //empty current pit
        playerPits[pitIndex] = 0;

        // the next pit to be sowed 1 stone
        int nextPit = pitIndex + 1;
        // if last stone hit the big Pit, get one more turn
        boolean oneMoreTurn = false;

        while (stonesToSow > 0) {
            nextPit = nextPit % (2 * MancalaDefault.PITS_NUMBER + 1);

            // next pit is in player's small pits
            if (nextPit <= MancalaDefault.PITS_NUMBER - 1) {

                /*
                 capturing stones : Always when the last stone lands in an own empty pit,
                 the player captures his own stone and all stones in the opposite pit (the opponent’s pit)
                 and puts them in his own (big or little?) pit
                */
                if (playerPits[nextPit] == 0 && stonesToSow == 1) {
                    // the opposite pit index of the empty pit of the last stone
                    int oppositePitIndex = MancalaDefault.PITS_NUMBER - nextPit - 1;

                    // capture the opposite stones to player's big pit, plus 1 stone of the sow
                    playerBigPit += opponentPits[oppositePitIndex] + 1;
                    opponentPits[oppositePitIndex] = 0;

                    // continue to the next pit
                    nextPit++;
                    stonesToSow--;
                    continue;
                }

                // sow 1 stone on the nextPit
                playerPits[nextPit] += 1;

                // continue to the next pit
                nextPit++;
                stonesToSow--;
                continue;
            }

            // next pit is on the player's big pit
            if (nextPit == MancalaDefault.PITS_NUMBER) {

                playerBigPit++;

                //get another turn if it is the last stone of the sow
                oneMoreTurn = stonesToSow == 1;

                // continue to the next pit
                nextPit++;
                stonesToSow--;
                continue;
            }

            // next pit is on the opponent's pits
            opponentPits[nextPit - MancalaDefault.PITS_NUMBER - 1] += 1;

            // continue to the next pit
            nextPit++;
            stonesToSow--;
        }

        //set the game board info after this sow
        if (isFirstPlayer) {
            gameBoard.setFirstBigPit(playerBigPit);
            gameBoard.setSecondPits(opponentPits);
            gameBoard.setFirstPits(playerPits);
            // if one more turn the next turn is still P1
            game.setGameStatus(oneMoreTurn ? GameStatus.ONGOING_P1 : GameStatus.ONGOING_P2);
        } else {
            gameBoard.setFirstPits(opponentPits);
            gameBoard.setSecondPits(playerPits);
            gameBoard.setSecondBigPit(playerBigPit);
            // if one more turn the next turn is still P2
            game.setGameStatus(oneMoreTurn ? GameStatus.ONGOING_P2 : GameStatus.ONGOING_P1);
        }
        game.setLastSowTime(LocalDateTime.now());
    }

    /**
     * check game is finished, canceled. and do the relevant calculation
     *
     * @param game game to be audited
     */
    void auditGame(Game game) {

        GameBoard gameBoard = game.getGameBoard();
        Player firstPlayer = game.getFirstPlayer();
        Player secondPlayer = game.getSecondPlayer();

        // check if game is over, at least one of the player's small pits have no stones at all.
        if (!Arrays.equals(new int[MancalaDefault.PITS_NUMBER], gameBoard.getFirstPits())
                && !Arrays.equals(new int[MancalaDefault.PITS_NUMBER], gameBoard.getSecondPits())) {

            log.info("audit that game is not finished yet, game = {}", game);
            return;
        }

        // settle the final numbers of all small and big pits
        int firstPlayerTotalScore = gameBoard.getFirstBigPit() + Arrays.stream(gameBoard.getFirstPits()).sum();
        int secondPlayerTotalScore = gameBoard.getSecondBigPit() + Arrays.stream(gameBoard.getSecondPits()).sum();
        gameBoard.setFirstPits(new int[MancalaDefault.PITS_NUMBER]);
        gameBoard.setSecondPits(new int[MancalaDefault.PITS_NUMBER]);
        gameBoard.setFirstBigPit(firstPlayerTotalScore);
        gameBoard.setSecondBigPit(secondPlayerTotalScore);

        if (firstPlayerTotalScore > secondPlayerTotalScore) {
            //first player win
            firstPlayer.setTotalMatch(firstPlayer.getTotalMatch() + 1);
            firstPlayer.setWinMatch(firstPlayer.getWinMatch() + 1);
            secondPlayer.setTotalMatch(secondPlayer.getTotalMatch() + 1);
            game.setGameStatus(GameStatus.FINISHED_WON_P1);
        } else if (firstPlayerTotalScore < secondPlayerTotalScore) {
            //second player win
            secondPlayer.setTotalMatch(secondPlayer.getTotalMatch() + 1);
            secondPlayer.setWinMatch(secondPlayer.getWinMatch() + 1);
            firstPlayer.setTotalMatch(firstPlayer.getTotalMatch() + 1);
            game.setGameStatus(GameStatus.FINISHED_WON_P2);
        } else {
            //tie game
            firstPlayer.setTotalMatch(firstPlayer.getTotalMatch() + 1);
            firstPlayer.setTieMatch(firstPlayer.getTieMatch() + 1);
            secondPlayer.setTotalMatch(secondPlayer.getTotalMatch() + 1);
            secondPlayer.setTieMatch(secondPlayer.getTieMatch() + 1);
            game.setGameStatus(GameStatus.FINISHED_TIE);
        }

        log.info("audit that game is finished, game = {}", game);
    }

    private boolean isOngoingGameStatus(GameStatus gameStatus) {

        return Arrays.asList(GameStatus.PENDING, GameStatus.ONGOING_P1, GameStatus.ONGOING_P2).contains(gameStatus);
    }

    private boolean isFinishedGameStatus(GameStatus gameStatus) {

        return Arrays.asList(GameStatus.FINISHED_TIE, GameStatus.FINISHED_WON_P1, GameStatus.FINISHED_WON_P2,
                GameStatus.FINISHED_CANCEL, GameStatus.FINISHED_QUIT_P1, GameStatus.FINISHED_QUIT_P2).contains(gameStatus);
    }
}
