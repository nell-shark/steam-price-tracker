package com.nellshark.backend.configs;

import com.nellshark.backend.clients.ApiSteamClient;
import com.nellshark.backend.clients.GoogleClient;
import com.nellshark.backend.clients.StoreSteamClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ClientConfig {

  @Bean
  public ApiSteamClient apiSteamClient() {
    RestClient restClient = RestClient.create("https://api.steampowered.com");
    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(ApiSteamClient.class);
  }

  @Bean
  public StoreSteamClient storeSteamClient() {
    RestClient restClient = RestClient.create("https://store.steampowered.com");
    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(StoreSteamClient.class);
  }

  @Bean
  public GoogleClient googleClient() {
    RestClient restClient = RestClient.create("https://www.google.com");
    return HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(restClient))
        .build()
        .createClient(GoogleClient.class);
  }
}
