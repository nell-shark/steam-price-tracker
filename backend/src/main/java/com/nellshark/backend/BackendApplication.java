package com.nellshark.backend;

import com.nellshark.backend.models.Game;
import com.nellshark.backend.repositories.GameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunnerBean(
            GameRepository gameRepository) {
        return (args) -> {
            System.out.println("In CommandLineRunnerImpl ");
            Game game = new Game();
            game.setId(620L);
            gameRepository.save(game);
        };
    }
}
