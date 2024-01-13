package com.nellshark.backend.controllers;

import com.nellshark.backend.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping
    private ResponseEntity<List<Long>> getAllGamesId() {
        return ResponseEntity.ok(gameService.getAllGameIds());
    }
}
