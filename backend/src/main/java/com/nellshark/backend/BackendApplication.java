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
            List<Game> gameList = List.of(
                    new Game(1086940L,
                            "Baldur's Gate",
                            "The classic adventure returns! Baldur’s Gate: Enhanced Edition includes " +
                                    "the original Baldur’s Gate adventure, the Tales of the Sword Coast expansion, " +
                                    "and all-new content including three new party members.",
                            "https://cdn.akamai.steamstatic.com/steam/apps/1086940/header.jpg?t=1703250718",
                            true,
                            false,
                            true),

                    new Game(252490L,
                            "Rust",
                            "The only aim in Rust is to survive. Everything wants you to die - " +
                                    "the island’s wildlife and other inhabitants, the environment, other survivors. " +
                                    "Do whatever it takes to last another night.",
                            "https://cdn.akamai.steamstatic.com/steam/apps/252490/header.jpg?t=1701938429",
                            true,
                            false,
                            true)
            );

            gameRepository.saveAll(gameList);
        };
    }
}
