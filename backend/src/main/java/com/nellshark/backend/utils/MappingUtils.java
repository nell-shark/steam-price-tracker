package com.nellshark.backend.utils;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.models.Game;

public final class MappingUtils {
    private MappingUtils() {
        throw new AssertionError();
    }

    public static GameDTO toDTO(Game game) {
        return new GameDTO(
                game.getId(),
                game.getName(),
                game.getHeaderImage()
        );
    }
}
