package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.requests.AuthRequest;
import com.nellshark.backend.dtos.responses.AuthResponse;
import com.nellshark.backend.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody AuthRequest authRequest) {
    return authService.register(authRequest);
  }

  @PostMapping("/login")
  public AuthResponse authenticate(@Valid @RequestBody AuthRequest authRequest) {
    return authService.authenticate(authRequest);
  }

  @PostMapping("/refresh-token")
  public AuthResponse refreshToken(HttpServletRequest request) {
    return authService.refreshToken(request);
  }
}
