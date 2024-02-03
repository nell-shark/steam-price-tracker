package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.services.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {
    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameDTO>> getAllGameDTOs() {
        return ResponseEntity.ok(gameService.getAllGameDTOs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable long id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }
}
