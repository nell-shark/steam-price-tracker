package com.nellshark.backend.services;


import com.nellshark.backend.exceptions.UserNotFoundException;
import com.nellshark.backend.models.User;
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

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) {
        return getUserByEmail(username);
    }

    public User getUserByEmail(String email) {
        log.info("Getting the user by email: {}", email);
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email='%s' wasn't found".formatted(email)));

    }

    public void createNewUser(@NonNull User user) {
        log.info("Creating new user: {}", user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.saveAndFlush(user);
    }
}
