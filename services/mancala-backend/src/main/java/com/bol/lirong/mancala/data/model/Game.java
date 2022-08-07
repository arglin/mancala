package com.bol.lirong.mancala.data.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */

@Data
@Document("games")
public class Game {

    /**
     * unique id of the game
     */
    @Id
    @Indexed(unique=true)
    private String gameId;

    /**
     *  board of the game
     */
    private GameBoard gameBoard;

    /**
     * first player who joins the game
     */
    private Player firstPlayer;

    /**
     * second player who joins the game
     */
    private Player secondPlayer;

    /**
     * status of the game life cycle
     */
    private GameStatus gameStatus;

    /**
     * the created time of the game,
     * init createTime when first player join the game
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * the start time of the game,
     * init startTime when the game starts (both players are there)
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * the time of the latest sow,
     * update lastSowTime whenever there is a sow
     */
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime lastSowTime;

    public Game() {
        this.gameId = UUID.randomUUID().toString();
        this.gameBoard = new GameBoard();
        this.gameStatus = GameStatus.PENDING;
    }
}
