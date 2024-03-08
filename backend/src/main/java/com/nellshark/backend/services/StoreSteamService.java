package com.nellshark.backend.services;

import com.nellshark.backend.clients.StoreSteamClient;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.GameType;
import com.nellshark.backend.models.clientresponses.AppDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreSteamService {
    private final StoreSteamClient storeSteamClient;
    private final BlockedGameService blockedGameService;

    @Nullable
    public Game getGameInfo(long id) {
        AppDetails appDetails = storeSteamClient.getAppDetails(id, null);
        AppDetails.App app = appDetails.getApp();

        if (!app.success()) {
            return null;
        }
        AppDetails.Data data = app.data();

        if (!EnumUtils.isValidEnumIgnoreCase(GameType.class, data.type())) {
            blockedGameService.addGameToBlockList(data.steamAppId());
            return null;
        }

        return new Game(
                data.steamAppId(),
                data.name(),
                GameType.valueOf(data.type().toUpperCase()),
                data.headerImage()
        );
    }
}
