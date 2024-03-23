package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.AppDTO;
import com.nellshark.backend.models.App;
import com.nellshark.backend.services.AppService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
  public List<AppDTO> getAllAppDTOs() {
    return appService.getAllAppDTOs();
  }

  @GetMapping("/{id}")
  public App getAppById(@PathVariable long id) {
    return appService.getAppById(id);
  }

  @GetMapping("/search")
  public List<AppDTO> getAppDTOsByPrefixName(@RequestParam("name") String prefixName) {
    return appService.getAppDTOsByPrefixName(prefixName);
  }
}
