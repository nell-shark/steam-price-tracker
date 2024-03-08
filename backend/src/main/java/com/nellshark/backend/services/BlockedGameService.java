package com.nellshark.backend.services;

import com.nellshark.backend.models.BlockedGame;
import com.nellshark.backend.repositories.BlockedGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockedGameService {
    private final BlockedGameRepository blockedGameRepository;

    public Set<Long> getBlockedGameIds() {
        log.info("Getting all blocked games");
        return blockedGameRepository.findAll()
                .stream()
                .map(BlockedGame::getId)
                .collect(Collectors.toSet());
    }

    public void addGameToBlockList(long gameId) {
        log.info("Adding game to block list: gameId={}", gameId);
        BlockedGame blockedGame = new BlockedGame(gameId);
        blockedGameRepository.save(blockedGame);
    }
}
