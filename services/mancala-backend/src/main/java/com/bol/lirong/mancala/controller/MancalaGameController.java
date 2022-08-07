package com.bol.lirong.mancala.controller;

import com.bol.lirong.mancala.exception.InvalidGameInputException;
import com.bol.lirong.mancala.exception.InvalidGameStateException;
import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.service.MancalaGameService;
import com.bol.lirong.mancala.service.MancalaMessagingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * @author linlirong
 * @created 26/02/2022
 * @project mancala
 */
@Controller
@AllArgsConstructor
@Slf4j
public class MancalaGameController {

    private MancalaGameService gameService;
    private MancalaMessagingService messagingService;

    @MessageMapping("/join")
    public void joinGame(Principal principal) {

        try {
            log.info("receive join by {}", principal.getName());
            Game game = this.gameService.playerJoin(principal.getName());
            this.messagingService.syncGameInfo(game);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @MessageMapping("/sow")
    public void sow(Principal principal, @Payload int payload) {

        try {
            log.info("receive sow payload = {} by {}", payload, principal.getName());
            Game game = this.gameService.playerSow(principal.getName(), payload);
            this.messagingService.syncGameInfo(game);
        } catch (InvalidGameStateException | InvalidGameInputException e) {
            this.messagingService.syncGameInfo(principal.getName(), e.getMessage());
            log.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }
}
