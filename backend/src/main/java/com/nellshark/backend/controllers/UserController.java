package com.nellshark.backend.controllers;

import com.nellshark.backend.models.User;
import com.nellshark.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Long> createNewUser(@Valid @RequestBody User user) {
        userService.createNewUser(user);
        return ResponseEntity.ok(user.getId());
    }
}
