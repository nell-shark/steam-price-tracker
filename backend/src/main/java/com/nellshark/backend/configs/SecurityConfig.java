package com.nellshark.backend.configs;

import com.nellshark.backend.models.entities.User;
import com.nellshark.backend.utils.Api.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      CsrfTokenRequestHandler csrfTokenRequestHandler,
      CorsConfigurationSource corsConfigurationSource,
      CustomAuthenticationFailureHandler customAuthenticationFailureHandler) throws Exception {
    return http
        .cors(cors -> cors
            .configurationSource(corsConfigurationSource)
        )
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(csrfTokenRequestHandler)
        )
        .formLogin(formLogin -> formLogin
            .loginProcessingUrl(Auth.BASE_URL + "/login")
            .usernameParameter("email")
            .passwordParameter("password")
            .successHandler((request, response, authentication) -> {
              response.setStatus(200);
              User user = (User) authentication.getPrincipal();
              response.getWriter().write(user.getId().toString());
            })
            .failureHandler(customAuthenticationFailureHandler)
        )
        .logout(logout -> logout
            .logoutUrl(Auth.BASE_URL + "/logout")
            .deleteCookies("JSESSIONID")
            .invalidateHttpSession(true)
            .logoutSuccessHandler((request, response, authentication) -> response.setStatus(204))
        )
        .build();
  }
}
