package com.nellshark.backend.services;

import com.nellshark.backend.dtos.requests.AuthRequest;
import com.nellshark.backend.dtos.requests.RegisterRequest;
import com.nellshark.backend.dtos.responses.AuthResponse;
import com.nellshark.backend.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

  private final UserService userService;
  private final CaptchaService captchaService;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  public AuthResponse register(@NonNull RegisterRequest registerRequest) {
    final String userEmail = registerRequest.email();
    log.info("Registering user: email='{}'", userEmail);

    userService.checkEmailAvailability(registerRequest.email());

    captchaService.verifyRecaptcha("clientCaptchaToken");

    final String encodedPassword = passwordEncoder.encode(registerRequest.password());

    User user = new User(userEmail, encodedPassword);
    userService.saveUser(user);

    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(jwtToken, refreshToken);
  }

  public AuthResponse authenticate(@NonNull AuthRequest request) {
    final String userEmail = request.email();
    log.info("Authenticating user: email='{}'", userEmail);

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userEmail,
            request.password()
        )
    );

    User user = userService.getUserByEmail(request.email());
    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(jwtToken, refreshToken);
  }
}
