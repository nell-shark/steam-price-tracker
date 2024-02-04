package com.nellshark.backend.configs;

import com.nellshark.backend.services.GameService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandLineRunnerConfig {
    @Bean
    public CommandLineRunner commandLineRunnerBean(
            GameService gameService) {
        return args -> gameService.checkForNewGamesPeriodically();
    }
}
