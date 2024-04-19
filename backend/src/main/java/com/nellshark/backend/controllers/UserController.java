package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.requests.FavoriteAppRequest;
import com.nellshark.backend.dtos.responses.AppResponse;
import com.nellshark.backend.services.UserService;
import com.nellshark.backend.utils.Api;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Api.User.BASE_URL)
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}/apps")
  @PreAuthorize("#id == authentication.principal.id OR hasRole('ADMIN')")
  public List<AppResponse> getFavoriteAppsByUserId(@PathVariable("id") long id) {
    return userService.getFavoriteAppsByUserId(id);
  }

  @PostMapping("/{id}/apps")
  @PreAuthorize("isAuthenticated()")
  @ResponseStatus(HttpStatus.CREATED)
  public void addFavoriteAppToUser(
      @PathVariable("id") long userId,
      @RequestBody FavoriteAppRequest favoriteAppRequest) {
    userService.addFavoriteAppToUser(userId, favoriteAppRequest.appId());
  }
}
