package com.nellshark.backend.repositories;

import com.nellshark.backend.models.GameBlocked;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameBlockedRepository extends JpaRepository<GameBlocked, Long> {
}
