package com.nellshark.backend;

import com.nellshark.backend.models.Game;
import com.nellshark.backend.repositories.GameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunnerBean(
            GameRepository gameRepository) {
        return (args) -> {
            Game game1 = new Game(620L);
            Game game2 = new Game(620L);
            Game game3 = new Game(620L);
            Game game4 = new Game(620L);
            Game game5 = new Game(620L);
            Game game6 = new Game(620L);
            Game game7 = new Game(620L);
            Game game8 = new Game(620L);
            Game game9 = new Game(620L);

            List<Game> gameList = List.of(
                    game1, game2, game3,
                    game4, game5, game6,
                    game7, game8, game9
            );

            gameRepository.saveAll(gameList);
        };
    }
}
