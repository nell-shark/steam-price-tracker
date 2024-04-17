package com.nellshark.backend.services;

import com.nellshark.backend.clients.GoogleClient;
import com.nellshark.backend.configs.CaptchaProperties;
import com.nellshark.backend.exceptions.ReCaptchaInvalidException;
import com.nellshark.backend.models.responses.RecaptchaResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CaptchaService {

  private final GoogleClient googleClient;
  private final CaptchaProperties captchaProperties;

  public void verifyRecaptcha(@NonNull String clientCaptchaToken) {
    log.info("Verify reCaptcha: {}", clientCaptchaToken);

    if (!captchaProperties.isEnabled()) {
      return;
    }

    if (StringUtils.isBlank(clientCaptchaToken)) {
      throw new ReCaptchaInvalidException("reCaptcha must not be blank");
    }

    RecaptchaResponse recaptchaResponse = googleClient.verifyRecaptcha(
        captchaProperties.getSecretKey(),
        clientCaptchaToken
    );

    if (!recaptchaResponse.success()) {
      throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
    }
  }
}
