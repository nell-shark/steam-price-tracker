package com.nellshark.backend.repositories;

import com.nellshark.backend.models.BlockedGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedGameRepository extends JpaRepository<BlockedGame, Long> {
}
