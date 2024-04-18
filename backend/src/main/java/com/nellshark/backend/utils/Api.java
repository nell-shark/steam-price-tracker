package com.nellshark.backend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Api {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Admin {

    public static final String BASE_URL = "/api/v1/admin";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class App {

    public static final String BASE_URL = "/api/v1/apps";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class User {

    public static final String BASE_URL = "/api/v1/users";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Auth {

    public static final String BASE_URL = "/api/v1/auth";
  }
}
