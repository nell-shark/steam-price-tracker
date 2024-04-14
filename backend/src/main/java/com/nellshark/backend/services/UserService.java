package com.nellshark.backend.services;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.dtos.UserRegistrationDTO;
import com.nellshark.backend.exceptions.EmailAlreadyTakenException;
import com.nellshark.backend.exceptions.UserNotFoundException;
import com.nellshark.backend.models.entities.App;
import com.nellshark.backend.models.entities.User;
import com.nellshark.backend.repositories.UserRepository;
import com.nellshark.backend.utils.MappingUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements AuthenticationProvider {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CaptchaService captchaService;
  private final AppService appService;
  private final HttpServletRequest request;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String userEmail = authentication.getName();
    log.info("Authenticating user: email='{}'", userEmail);

    String captcha = request.getParameter("captcha");
    captchaService.verifyRecaptcha(captcha);

    User user = getUserByEmail(userEmail);

    String password = authentication.getCredentials().toString();
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new BadCredentialsException("Invalid credentials for user: " + userEmail);
    }

    return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(UsernamePasswordAuthenticationToken.class);
  }

  public User getUserByEmail(@NonNull String email) {
    log.info("Getting user by email: {}", email);
    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UserNotFoundException("User with email='" + email + "' not found")
        );
  }

  private User getUserById(long id) {
    log.info("Getting user by id: {}", id);
    return userRepository.
        findById(id)
        .orElseThrow(() -> new UserNotFoundException("User not found for ID: " + id));
  }

  public long registerUser(
      @NonNull UserRegistrationDTO userRegistrationDTO,
      @NonNull String clientCaptchaToken) {
    String userEmail = userRegistrationDTO.email();
    log.info("Registering user: email='{}'", userEmail);

    checkEmailAvailability(userRegistrationDTO.email());

    captchaService.verifyRecaptcha(clientCaptchaToken);

    String encodedPassword = passwordEncoder.encode(userRegistrationDTO.password());

    User newUser = new User(userEmail, encodedPassword);
    newUser = userRepository.saveAndFlush(newUser);

    return newUser.getId();
  }

  private void checkEmailAvailability(@NonNull String email) {
    if (userRepository.isEmailTaken(email)) {
      throw new EmailAlreadyTakenException("Email '" + email + "' is already taken");
    }
  }

  public void addFavoriteAppToUser(long userId, long appId) {
    log.info("Adding favorite app to user: userId={}, appId={}", userId, appId);
    User userById = getUserById(userId);
    App appById = appService.getAppById(appId);
    userById.getFavoriteApps().add(appById);
    userRepository.save(userById);
  }

  public List<AppDTO> getFavoriteAppsByUserId(long id) {
    log.info("Getting favorite apps by user id: {}", id);
    User user = getUserById(id);
    return user.getFavoriteApps()
        .stream()
        .map(MappingUtils::toAppDTO)
        .toList();
  }
}
