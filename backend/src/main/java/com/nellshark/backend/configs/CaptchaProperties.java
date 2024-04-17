package com.nellshark.backend.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "google.captcha.key")
public class CaptchaProperties {

  private boolean enabled;
  private String secretKey;
}
