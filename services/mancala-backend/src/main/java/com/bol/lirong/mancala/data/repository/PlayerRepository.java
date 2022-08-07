package com.bol.lirong.mancala.data.repository;

import com.bol.lirong.mancala.data.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

/**
 * @author linlirong
 * @created 24/02/2022
 * @project mancala
 */
public interface PlayerRepository extends MongoRepository<Player, String> {

    Optional<Player> findPlayerByPlayerId(String playerId);

    void deletePlayerByPlayerId(String playerId);
}
