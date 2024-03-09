package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.services.GameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
public class GameController {

  private final GameService gameService;

  @GetMapping
  public List<GameDTO> getAllGameDTOs() {
    return gameService.getAllGameDTOs();
  }

  @GetMapping("/{id}")
  public Game getGameById(@PathVariable long id) {
    return gameService.getGameById(id);
  }

  @GetMapping("/search")
  public List<GameDTO> getGameDTOsByPrefixName(@RequestParam("name") String prefixName) {
    return gameService.getGameDTOsByPrefixName(prefixName);
  }
}
