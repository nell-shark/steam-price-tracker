package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.models.App;
import com.nellshark.backend.services.AppService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
  public Page<AppDTO> getAppDTOsByPage(@RequestParam(value = "page", defaultValue = "1") int page) {
    return appService.getAppDTOsByPage(page);
  }

  @GetMapping("/search")
  public Page<AppDTO> getAppDTOsByPrefixName(
      @RequestParam("name") String prefixName,
      @RequestParam(value = "page", defaultValue = "1") int page) {
    return appService.getAppDTOsByPrefixName(prefixName, page);
  }

  @GetMapping("/{id}")
  public App getAppById(@PathVariable long id) {
    return appService.getAppById(id);
  }
}
