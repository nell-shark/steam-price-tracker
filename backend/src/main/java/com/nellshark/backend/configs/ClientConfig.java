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
    RestClient client = RestClient.create("https://api.steampowered.com");
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(client))
        .build();
    return factory.createClient(ApiSteamClient.class);
  }

  @Bean
  public StoreSteamClient storeSteamClient() {
    RestClient client = RestClient.create("https://store.steampowered.com");
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(client))
        .build();
    return factory.createClient(StoreSteamClient.class);
  }

  @Bean
  public GoogleClient googleClient() {
    RestClient client = RestClient.create("https://www.google.com");
    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builderFor(RestClientAdapter.create(client))
        .build();
    return factory.createClient(GoogleClient.class);
  }
}
