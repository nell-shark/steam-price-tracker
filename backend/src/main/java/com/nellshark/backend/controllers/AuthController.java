package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.requests.AuthRequest;
import com.nellshark.backend.dtos.requests.RegisterRequest;
import com.nellshark.backend.dtos.responses.AuthResponse;
import com.nellshark.backend.services.AuthService;
import com.nellshark.backend.utils.Api;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Api.Auth.BASE_URL)
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
    return authService.register(registerRequest);
  }

  @PostMapping("/login")
  public AuthResponse authenticate(@Valid @RequestBody AuthRequest authRequest) {
    return authService.authenticate(authRequest);
  }
}
