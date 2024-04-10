package com.nellshark.backend.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      CsrfTokenRequestHandler csrfTokenRequestHandler,
      CorsConfigurationSource corsConfigurationSource) throws Exception {
    return http
        .cors(cors -> cors
            .configurationSource(corsConfigurationSource)
        )
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(csrfTokenRequestHandler)
        )
        .formLogin(formLogin -> formLogin
            .loginProcessingUrl("/api/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .successHandler((request, response, authentication) -> response.setStatus(200))
        )
        .logout(logout -> logout
            .logoutUrl("/api/logout")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .logoutSuccessHandler((request, response, authentication) -> response.setStatus(200))
        )
        .build();
  }
}
