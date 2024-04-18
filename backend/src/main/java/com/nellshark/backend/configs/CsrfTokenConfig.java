package com.nellshark.backend.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

@Configuration
public class CsrfTokenConfig {

  @Bean
  public CsrfTokenRequestHandler csrfTokenRequestHandler() {
    XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
    delegate.setCsrfRequestAttributeName(null);
//    noinspection FunctionalExpressionCanBeFolded
    return delegate::handle;
  }
}
