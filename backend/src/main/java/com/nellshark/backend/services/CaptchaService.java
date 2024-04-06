package com.nellshark.backend.services;

import com.nellshark.backend.clients.GoogleClient;
import com.nellshark.backend.configs.CaptchaProperties;
import com.nellshark.backend.exceptions.ReCaptchaInvalidException;
import com.nellshark.backend.models.responses.RecaptchaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaptchaService {

  private final GoogleClient googleClient;
  private final CaptchaProperties captchaProperties;

  public void verifyRecaptcha(@NonNull String clientCaptchaToken) {
    RecaptchaResponse recaptchaResponse = googleClient.verifyRecaptcha(
        captchaProperties.getSecret(),
        clientCaptchaToken
    );

    if (!recaptchaResponse.success()) {
      throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
    }
  }
}
