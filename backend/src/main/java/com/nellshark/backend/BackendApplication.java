package com.nellshark.backend;

import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.GameRepository;
import com.nellshark.backend.repositories.PriceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;

@SpringBootApplication
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunnerBean(
            GameRepository gameRepository,
            PriceRepository priceRepository
    ) {
        return (args) -> {
            System.out.println("In CommandLineRunnerImpl ");
            Game game = new Game();
            game.setId(1L);
            gameRepository.save(game);


            Price price = new Price(1L, 4f, LocalDate.now(), game);
            priceRepository.save(price);
        };
    }
}
