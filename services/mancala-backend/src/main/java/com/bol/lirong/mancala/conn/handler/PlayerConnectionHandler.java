package com.bol.lirong.mancala.conn.handler;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.service.MancalaGameService;
import com.bol.lirong.mancala.service.MancalaMessagingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
@Component
@Slf4j
@AllArgsConstructor
public class PlayerConnectionHandler {

    private MancalaGameService gameService;
    private MancalaMessagingService messagingService;

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) {

        String playerId = Objects.requireNonNull(event.getUser()).getName();
        log.info("new Player connected! playerId = {}", playerId);
        this.gameService.playerConnected(playerId);
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {

        String playerId = Objects.requireNonNull(event.getUser()).getName();
        log.info("Player disconnected! playerId = {}", Objects.requireNonNull(event.getUser()).getName());
        Game game = this.gameService.playerDisConnected(playerId);
        this.messagingService.syncGameInfo(game);
    }
}
