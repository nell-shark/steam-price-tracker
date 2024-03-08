package com.nellshark.backend.services;

import com.nellshark.backend.clients.ApiSteamClient;
import com.nellshark.backend.models.clientresponses.AppList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiSteamService {
    private final ApiSteamClient apiSteamClient;

    public Set<Long> getAllSteamGameIds() {
        log.info("Getting all games id from steam api");
        return apiSteamClient.getAppList()
                .appListRoot()
                .apps()
                .stream()
                .filter(Objects::nonNull)
                .filter(app -> StringUtils.isNotBlank(app.name()))
                .map(AppList.App::appId)
                .collect(Collectors.toSet());
    }
}
