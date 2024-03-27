package com.nellshark.backend;

import com.nellshark.backend.models.User;
import com.nellshark.backend.models.UserRole;
import com.nellshark.backend.services.UserService;
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
  public CommandLineRunner commandLineRunner(UserService userService) {
    return args -> {
      User user = new User("admin@gmail.com", "password123");
      user.setRole(UserRole.ROLE_ADMIN);
      userService.createNewUser(user);
    };
  }
}
