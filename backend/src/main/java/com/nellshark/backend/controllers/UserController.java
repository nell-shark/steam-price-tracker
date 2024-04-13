package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.FavoriteAppRequestDTO;
import com.nellshark.backend.dtos.UserRegistrationDTO;
import com.nellshark.backend.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Long registerUser(
      @Valid @RequestBody UserRegistrationDTO user,
      @Valid @NotBlank @RequestParam("captcha") String clientCaptchaToken) {
    return userService.registerUser(user, clientCaptchaToken);
  }

  @PostMapping("/{id}/apps")
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(HttpStatus.CREATED)
  public void addFavoriteAppToUser(
      @PathVariable("id") long userId,
      @RequestBody FavoriteAppRequestDTO favoriteAppRequestDTO) {
    userService.addFavoriteAppToUser(userId, favoriteAppRequestDTO.appId());
  }
}
