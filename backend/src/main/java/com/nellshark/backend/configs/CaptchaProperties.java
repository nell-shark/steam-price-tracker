package com.nellshark.backend.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google.captcha.key")
@Data
public class CaptchaProperties {

  private String secret;
}
