package com.nellshark.backend.services;

import com.nellshark.backend.models.GameBlocked;
import com.nellshark.backend.repositories.GameBlockedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameBlockedService {
    private final GameBlockedRepository gameBlockedRepository;

    public List<Long> getGameBlockedIds() {
        log.info("Getting all blocked games");
        return gameBlockedRepository.findAll()
                .stream()
                .map(GameBlocked::getId)
                .toList();
    }

    public void addGameToBlockList(long gameId) {
        log.info("Adding game to blacklist: gameId={}", gameId);
        GameBlocked blockedGame = new GameBlocked(gameId);
        gameBlockedRepository.save(blockedGame);
    }
}
