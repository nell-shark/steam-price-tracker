package com.nellshark.backend.controllers;

import com.nellshark.backend.dtos.requests.AppBlockRequest;
import com.nellshark.backend.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Secured("ROLE_ADMIN")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @PostMapping
  public void addAppToBlockList(@RequestBody AppBlockRequest appBlockRequest) {
    adminService.addAppToBlockList(appBlockRequest.id());
  }
}
