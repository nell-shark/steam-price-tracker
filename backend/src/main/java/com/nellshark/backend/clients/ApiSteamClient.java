package com.nellshark.backend.clients;

import com.nellshark.backend.models.clientresponses.AppList;
import org.springframework.stereotype.Component;
import org.springframework.web.service.annotation.GetExchange;

@Component
public interface ApiSteamClient {
    @GetExchange("/ISteamApps/GetAppList/v2")
    AppList getAppList();
}
