package com.nellshark.backend.services;

import com.nellshark.backend.dtos.UserRegistrationDTO;
import com.nellshark.backend.exceptions.EmailAlreadyTakenException;
import com.nellshark.backend.exceptions.UserNotFoundException;
import com.nellshark.backend.models.entities.User;
import com.nellshark.backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CaptchaService captchaService;

  @Override
  public UserDetails loadUserByUsername(@NonNull String username) {
    return getUserByEmail(username);
  }

  public User getUserByEmail(String email) {
    log.info("Getting the user by email: {}", email);
    return userRepository
        .findByEmail(email)
        .orElseThrow(
            () -> new UserNotFoundException("User with email='%s' wasn't found".formatted(email)));

  }

  public long registerUser(
      @NonNull UserRegistrationDTO userRegistrationDTO,
      @NonNull String clientCaptchaToken) {
    log.info("Register user: email={}", userRegistrationDTO.email());

    checkEmailAvailability(userRegistrationDTO.email());

    captchaService.verifyRecaptcha(clientCaptchaToken);

    String encodedPassword = passwordEncoder.encode(userRegistrationDTO.password());

    User user = userRepository.saveAndFlush(new User(userRegistrationDTO.email(), encodedPassword));

    return user.getId();
  }

  private void checkEmailAvailability(@NonNull String email) {
    if (userRepository.isEmailTaken(email)) {
      throw new EmailAlreadyTakenException("Email '%s' is already taken".formatted(email));
    }
  }
}
