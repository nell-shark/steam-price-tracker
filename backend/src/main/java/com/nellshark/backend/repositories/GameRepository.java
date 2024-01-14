package com.nellshark.backend.repositories;

import com.nellshark.backend.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT game.id " +
            "FROM Game game")
    List<Long> getAllGameIds();

    @Query("SELECT game " +
            "FROM Game game " +
            "LEFT JOIN FETCH game.prices " +
            "WHERE game.id = :gameId")
    Optional<Game> findByIdWithPrices(Long gameId);
}
