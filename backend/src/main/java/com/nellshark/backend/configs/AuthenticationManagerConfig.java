package com.nellshark.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class AuthenticationManagerConfig {

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    return http.getSharedObject(AuthenticationManagerBuilder.class).build();
  }
}
