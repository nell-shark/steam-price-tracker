package com.nellshark.backend.services;

import com.nellshark.backend.dtos.requests.AuthRequest;
import com.nellshark.backend.dtos.responses.AuthResponse;
import com.nellshark.backend.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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

  public AuthResponse register(@NonNull AuthRequest authRequest) {
    final String userEmail = authRequest.email();
    log.info("Registering user: email='{}'", userEmail);

    userService.checkEmailAvailability(authRequest.email());

    captchaService.verifyRecaptcha("clientCaptchaToken");

    final String encodedPassword = passwordEncoder.encode(authRequest.password());
    User user = new User(userEmail, encodedPassword);
    userService.saveUser(user);

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(accessToken, refreshToken);
  }

  public AuthResponse login(@NonNull AuthRequest request) {
    final String userEmail = request.email();
    log.info("Authenticating user: email='{}'", userEmail);

    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            userEmail,
            request.password()
        )
    );

    User user = userService.getUserByEmail(request.email());
    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(accessToken, refreshToken);
  }

  public AuthResponse refreshToken(@NonNull String authHeader) {
    log.info("Refreshing token");

    if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
      throw new BadCredentialsException("Invalid or missing Authorization header");
    }

    String refreshToken = authHeader.substring(7);
    String userEmail = jwtService.extractUsername(refreshToken);

    if (StringUtils.isBlank(userEmail)) {
      throw new BadCredentialsException("Unable to extract user email from refresh token");
    }

    User user = userService.getUserByEmail(userEmail);
    if (!jwtService.isTokenValid(refreshToken, user)) {
      throw new BadCredentialsException("Refresh token is invalid");
    }

    String newAccessToken = jwtService.generateToken(user);
    String newRefreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(newAccessToken, newRefreshToken);
  }
}
