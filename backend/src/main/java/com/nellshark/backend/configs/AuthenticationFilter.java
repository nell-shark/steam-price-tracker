package com.nellshark.backend.configs;

import com.nellshark.backend.services.CaptchaService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

  private final CaptchaService captchaService;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    captchaService.verifyRecaptcha(request.getParameter("captcha"));
    super.doFilter(request, response, chain);
  }

  @Override
  @Autowired
  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    super.setAuthenticationManager(authenticationManager);
  }
}
