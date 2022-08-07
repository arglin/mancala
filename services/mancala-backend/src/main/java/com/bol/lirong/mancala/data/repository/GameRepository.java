package com.bol.lirong.mancala.data.repository;

import com.bol.lirong.mancala.data.model.Game;
import com.bol.lirong.mancala.data.model.GameStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */
public interface GameRepository extends MongoRepository<Game, String> {

    Optional<List<Game>> findGamesByGameStatus(GameStatus gameStatus);

    Optional<Game> findGameByGameId(String gameId);
}
