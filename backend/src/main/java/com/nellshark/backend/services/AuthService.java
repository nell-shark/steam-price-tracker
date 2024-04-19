package com.nellshark.backend.services;

import com.nellshark.backend.dtos.requests.AuthRequest;
import com.nellshark.backend.dtos.responses.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthResponse authenticate(AuthRequest request) {
    log.info("authenticate");

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.email(),
            request.password()
        )
    );

    var user = userService.getUserByEmail(request.email());
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(jwtToken, refreshToken);
  }
}
