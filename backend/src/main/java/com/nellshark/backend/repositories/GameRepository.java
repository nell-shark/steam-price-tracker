package com.nellshark.backend.repositories;

import com.nellshark.backend.models.Game;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    @Override
    @NotNull
    @EntityGraph(attributePaths = {
            "operatingSystemRequirements",
            "prices"
    })
    Optional<Game> findById(@NotNull Long id);

    @Query("""
             SELECT g.id
             FROM Game g
            """)
    List<Long> findAllIds();

    @Query("""
             SELECT g
             FROM Game g
             WHERE LOWER(g.name)
             LIKE LOWER(CONCAT(:prefixName, '%'))
            """)
    List<Game> findByNameStartsWithIgnoreCase(@NotNull String prefixName);
}
