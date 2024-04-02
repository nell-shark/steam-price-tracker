package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.UserRequestDTO;
import com.nellshark.backend.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Long createNewUser(
      @RequestBody UserRequestDTO userRequestDTO,
      HttpServletRequest request) {
    String response = request.getParameter("g-recaptcha-response");
    System.out.println("response = " + response);
    return userService.createNewUser(userRequestDTO);
  }
}
