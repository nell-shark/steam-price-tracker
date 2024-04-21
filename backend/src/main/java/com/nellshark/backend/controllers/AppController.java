package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.responses.AppResponse;
import com.nellshark.backend.models.App;
import com.nellshark.backend.services.AppService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/apps")
@RequiredArgsConstructor
public class AppController {

  private final AppService appService;

  @GetMapping
  public Page<AppResponse> getAppDTOsByPage(
      @RequestParam(value = "page", defaultValue = "1") int page) {
    return appService.getAppDTOsByPage(page);
  }

  @GetMapping("/search")
  public Page<AppResponse> getAppDTOsByPrefixName(
      @Valid @NotBlank @RequestParam("name") String prefixName,
      @RequestParam(value = "page", defaultValue = "1") int page) {
    return appService.getAppDTOsByPrefixName(prefixName, page);
  }

  @GetMapping("/{id}")
  public App getAppById(@PathVariable long id) {
    return appService.getAppById(id);
  }

  @GetMapping("/test")
  @Secured("ROLE_USER")
  public String test() {
    return "well, well, well.";
  }
}
