package com.nellshark.backend.configs.filters;

import com.nellshark.backend.models.User;
import com.nellshark.backend.services.JwtService;
import com.nellshark.backend.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserService userService;
  private final HandlerExceptionResolver handlerExceptionResolver;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }

    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    String token = null;
    String username = null;
    try {
      if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
        token = authHeader.substring(7);
        username = jwtService.extractUsername(token);
      }

      if (StringUtils.isNotBlank(username)
          && SecurityContextHolder.getContext().getAuthentication() == null) {
        User user = userService.getUserByEmail(username);
        if (jwtService.isTokenValid(token, user)) {
          var authenticationToken = new UsernamePasswordAuthenticationToken(
              user,
              null,
              user.getAuthorities()
          );
          authenticationToken.setDetails(
              new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
      }
    } catch (RuntimeException exception) {
      handlerExceptionResolver.resolveException(request, response, null, exception);
    }

    filterChain.doFilter(request, response);
  }
}
