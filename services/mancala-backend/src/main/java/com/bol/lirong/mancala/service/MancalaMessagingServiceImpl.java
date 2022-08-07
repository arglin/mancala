package com.bol.lirong.mancala.service;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.Player;
import com.bol.lirong.mancala.data.repository.GameRepository;
import com.bol.lirong.mancala.data.repository.PlayerRepository;
import com.bol.lirong.mancala.data.response.GameResponse;
import com.bol.lirong.mancala.settings.MancalaDefault;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
@Service
@AllArgsConstructor
@Slf4j
public class MancalaMessagingServiceImpl implements MancalaMessagingService {

    private SimpMessagingTemplate messagingTemplate;
    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    /**
     * sync game info to first player and second player
     *
     * @param game Mancala Game
     */
    @Override
    public void syncGameInfo(Game game) {

        if (game == null) return;
        GameResponse firstPlayerGameResponse = new GameResponse(game, true);
        GameResponse secondPlayerGameResponse = new GameResponse(game, false);
        this.syncGameWithCheck(game, firstPlayerGameResponse);
        this.syncGameWithCheck(game, secondPlayerGameResponse);
    }

    private void syncGameWithCheck(Game game, GameResponse playerGameResponse) {
        if (playerGameResponse.getMe() != null) {
            Optional<Player> playerOptional =
                    this.playerRepository.findPlayerByPlayerId(playerGameResponse.getMe().getPlayerId());
            //send when gameId of player in DB is the same gameId of the gameResponse to sync
            playerOptional.ifPresentOrElse((player -> {
                if (player.getGameId() != null && player.getGameId().equals(game.getGameId())) {
                    messagingTemplate.convertAndSendToUser(player.getPlayerId(), MancalaDefault.GAME_INFO_SUB,
                            playerGameResponse);
                }
            }), ()->{
                log.info("cannot find player {} in DB", playerGameResponse.getMe().getPlayerId());
            });
        }
    }

    /**
     * sync game info with error message to Player
     *
     * @param playerId playerId of the player with error
     * @param errMsg   error message
     */
    @Override
    public void syncGameInfo(String playerId, String errMsg) {

        if (playerId == null) return;
        Optional<Player> playerOptional = this.playerRepository.findPlayerByPlayerId(playerId);
        playerOptional.ifPresentOrElse(player -> {
            Optional<Game> gameOptional = this.gameRepository.findGameByGameId(player.getGameId());
            gameOptional.ifPresentOrElse(game -> {

                GameResponse firstPlayerGameResponse = new GameResponse(game, true);
                GameResponse secondPlayerGameResponse = new GameResponse(game, false);

                if (firstPlayerGameResponse.getMe() != null && playerId.equals(firstPlayerGameResponse.getMe().getPlayerId())) {
                    //set the errMsg to first player
                    firstPlayerGameResponse.getMe().setErrorMsg(errMsg);
                    messagingTemplate.convertAndSendToUser(firstPlayerGameResponse.getMe().getPlayerId(),
                            MancalaDefault.GAME_INFO_SUB, firstPlayerGameResponse);
                }
                if (secondPlayerGameResponse.getMe() != null && playerId.equals(secondPlayerGameResponse.getMe().getPlayerId())) {
                    //set the errMsg to second player
                    secondPlayerGameResponse.getMe().setErrorMsg(errMsg);
                    messagingTemplate.convertAndSendToUser(secondPlayerGameResponse.getMe().getPlayerId(),
                            MancalaDefault.GAME_INFO_SUB, secondPlayerGameResponse);
                }
                log.info("send errMsg = {}, tp playerId = {}", errMsg, playerId);
            }, () -> log.error("sync game with error, cannot find game by gameId = {}", player.getGameId()));
        }, () -> log.error("sync game with error, cannot find player by playerId = {}", playerId));
    }
}
